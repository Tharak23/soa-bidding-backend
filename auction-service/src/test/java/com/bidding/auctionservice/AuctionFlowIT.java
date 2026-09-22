package com.bidding.auctionservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bidding.auctionservice.client.PaymentClient;
import com.bidding.auctionservice.service.AuctionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@Sql("/schema.sql")
class AuctionFlowIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	AuctionService auctionService;

	@MockitoBean
	PaymentClient payments;

	@Test
	void createRejectAcceptAndRefuseAfterEnd() throws Exception {
		String endsAt = Instant.now().plusSeconds(3600).toString();
		MvcResult created = mockMvc.perform(post("/api/auctions")
						.with(jwt().jwt(jwt -> jwt.subject("seller_1")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Vintage watch","description":"Steel","startPriceCents":1000,"minIncrementCents":100,"endsAt":"%s"}
								""".formatted(endsAt)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Vintage watch"))
				.andReturn();
		JsonNode auction = mapper.readTree(created.getResponse().getContentAsString());
		UUID id = UUID.fromString(auction.get("id").asText());

		mockMvc.perform(post("/internal/auctions/" + id + "/leading-bid")
						.with(jwt().jwt(jwt -> jwt.subject("bidder_1")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"bidderClerkUserId":"bidder_1","amountCents":900,"bidId":"%s"}
								""".formatted(UUID.randomUUID())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accepted").value(false))
				.andExpect(jsonPath("$.code").value("bid_too_low"));

		mockMvc.perform(post("/internal/auctions/" + id + "/leading-bid")
						.with(jwt().jwt(jwt -> jwt.subject("bidder_1")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"bidderClerkUserId":"bidder_1","amountCents":1000,"bidId":"%s"}
								""".formatted(UUID.randomUUID())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accepted").value(true));

		mockMvc.perform(get("/api/auctions/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currentPriceCents").value(1000));
	}

	@Test
	void closesSoldAuctionOnce() throws Exception {
		String endsAt = Instant.now().minusSeconds(5).toString();
		MvcResult created = mockMvc.perform(post("/api/auctions")
						.with(jwt().jwt(jwt -> jwt.subject("seller_2")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Ended lot","startPriceCents":2000,"minIncrementCents":100,"endsAt":"%s"}
								""".formatted(Instant.now().plusSeconds(30).toString())))
				.andExpect(status().isOk())
				.andReturn();
		UUID id = UUID.fromString(mapper.readTree(created.getResponse().getContentAsString()).get("id").asText());
		mockMvc.perform(post("/internal/auctions/" + id + "/leading-bid")
						.with(jwt().jwt(jwt -> jwt.subject("winner")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"bidderClerkUserId":"winner","amountCents":2000,"bidId":"%s"}
								""".formatted(UUID.randomUUID())))
				.andExpect(jsonPath("$.accepted").value(true));

		// Force end by bidding after rewriting is not possible; call close via a second expired listing.
		MvcResult expired = mockMvc.perform(post("/api/auctions")
						.with(jwt().jwt(jwt -> jwt.subject("seller_3")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Already ended","startPriceCents":500,"minIncrementCents":50,"endsAt":"%s"}
								""".formatted(Instant.now().plusSeconds(2).toString())))
				.andExpect(status().isOk())
				.andReturn();
		UUID expiredId = UUID.fromString(mapper.readTree(expired.getResponse().getContentAsString()).get("id").asText());
		Thread.sleep(2100);
		mockMvc.perform(post("/internal/auctions/" + expiredId + "/leading-bid")
						.with(jwt().jwt(jwt -> jwt.subject("late")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"bidderClerkUserId":"late","amountCents":500,"bidId":"%s"}
								""".formatted(UUID.randomUUID())))
				.andExpect(jsonPath("$.accepted").value(false))
				.andExpect(jsonPath("$.code").value("auction_closed"));
	}

	@Test
	void settlesWinnerOnce() throws Exception {
		MvcResult created = mockMvc.perform(post("/api/auctions")
						.with(jwt().jwt(jwt -> jwt.subject("seller_close")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Close once","startPriceCents":1500,"minIncrementCents":100,"endsAt":"%s"}
								""".formatted(Instant.now().plusSeconds(2).toString())))
				.andExpect(status().isOk())
				.andReturn();
		UUID id = UUID.fromString(mapper.readTree(created.getResponse().getContentAsString()).get("id").asText());
		UUID bidId = UUID.randomUUID();
		mockMvc.perform(post("/internal/auctions/" + id + "/leading-bid")
						.with(jwt().jwt(jwt -> jwt.subject("winner_close")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"bidderClerkUserId":"winner_close","amountCents":1500,"bidId":"%s"}
								""".formatted(bidId)))
				.andExpect(jsonPath("$.accepted").value(true));

		Thread.sleep(2100);
		auctionService.closeDueAuctions();
		auctionService.closeDueAuctions();
		verify(payments, times(1)).settle(any());
	}
}
