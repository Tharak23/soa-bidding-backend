package com.bidding.paymentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bidding.paymentservice.domain.LedgerEntry;
import com.bidding.paymentservice.domain.Profile;
import com.bidding.paymentservice.domain.Wallet;
import com.bidding.paymentservice.repo.LedgerEntryRepository;
import com.bidding.paymentservice.repo.ProfileRepository;
import com.bidding.paymentservice.repo.WalletRepository;
import com.bidding.paymentservice.repo.WebhookEventRepository;
import com.bidding.paymentservice.service.WalletService;
import com.bidding.paymentservice.web.HoldRequest;
import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.services.blocking.WebhookService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.Mockito;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@Sql("/schema.sql")
class WalletFlowIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ProfileRepository profiles;

	@Autowired
	WalletRepository wallets;

	@Autowired
	LedgerEntryRepository ledger;

	@Autowired
	WebhookEventRepository webhookEvents;

	@Autowired
	WalletService walletService;

	@MockitoBean
	DodoPaymentsClient dodo;

	@Test
	void holdInsufficientThenHoldAndDuplicateWebhook() throws Exception {
		Profile profile = newProfile("bidder_wallet");
		Wallet wallet = new Wallet();
		wallet.setProfileId(profile.getId());
		wallet.setClerkUserId("bidder_wallet");
		wallet.setAvailableBalanceCents(500);
		wallets.save(wallet);

		mockMvc.perform(get("/api/wallet").with(jwt().jwt(jwt -> jwt.subject("bidder_wallet"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.availableBalanceCents").value(500));

		UUID bidId = UUID.randomUUID();
		UUID auctionId = UUID.randomUUID();
		try {
			walletService.hold(new HoldRequest("bidder_wallet", 900, auctionId, bidId, "hold:" + bidId));
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("cannot cover") || ex.getMessage().contains("insufficient"));
		}

		UUID goodBid = UUID.randomUUID();
		walletService.hold(new HoldRequest("bidder_wallet", 400, auctionId, goodBid, "hold:" + goodBid));
		Wallet after = wallets.findByClerkUserId("bidder_wallet").orElseThrow();
		assertEquals(100, after.getAvailableBalanceCents());
		assertEquals(400, after.getHeldBalanceCents());

		WebhookService webhooks = Mockito.mock(WebhookService.class);
		Mockito.when(dodo.webhooks()).thenReturn(webhooks);
		Mockito.when(webhooks.unwrap(Mockito.any(com.dodopayments.api.core.UnwrapWebhookParams.class))).thenReturn(null);

		String body = """
				{"type":"payment.succeeded","data":{"payment_id":"pay_dup","total_amount":2500,"metadata":{"clerkUserId":"bidder_wallet"}}}
				""";
		walletService.handleWebhook(body, "wh_1", "sig", "1");
		walletService.handleWebhook(body, "wh_1", "sig", "1");
		assertEquals(1, webhookEvents.findByWebhookId("wh_1").stream().count());
		assertEquals(1, ledger.findByClerkUserIdOrderByCreatedAtDesc("bidder_wallet").stream()
				.filter(entry -> "top_up".equals(entry.getEntryType()))
				.count());
	}

	private Profile newProfile(String clerkUserId) {
		return profiles.findByClerkUserId(clerkUserId).orElseGet(() -> {
			Profile profile = new Profile();
			profile.setId(UUID.randomUUID());
			profile.setClerkUserId(clerkUserId);
			return profiles.save(profile);
		});
	}
}
