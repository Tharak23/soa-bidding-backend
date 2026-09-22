package com.bidding.auctionservice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bidding.auctionservice.domain.Auction;
import com.bidding.auctionservice.domain.AuctionStatus;
import com.bidding.auctionservice.repo.AuctionRepository;

@Component
@Profile("!test")
public class AuctionSeed implements ApplicationRunner {

	private static final String SELLER = "seed_seller";

	private final AuctionRepository auctions;

	public AuctionSeed(AuctionRepository auctions) {
		this.auctions = auctions;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (!auctions.findBySellerClerkUserIdOrderByCreatedAtDesc(SELLER).isEmpty()) {
			return;
		}
		Instant now = Instant.now();
		List<Auction> lots = List.of(
				lot("Leica M6 body", "Mechanical 35mm rangefinder, meter works, light brassing on the top plate.",
						"https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=1400&q=80",
						"Cameras", "Used - Good", "Berlin", 85000, 2500, now.plus(6, ChronoUnit.HOURS)),
				lot("Rolex Submariner 16610", "1999 two-liner, box and papers, service papers from 2022.",
						"https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=1400&q=80",
						"Watches", "Used - Excellent", "Geneva", 720000, 10000, now.plus(1, ChronoUnit.DAYS)),
				lot("Fender Stratocaster 1964", "Sunburst slab-board, original pickups, professionally set up.",
						"https://images.unsplash.com/photo-1510915361894-dbf53f4d349b?auto=format&fit=crop&w=1400&q=80",
						"Instruments", "Used - Good", "Nashville", 185000, 5000, now.plus(2, ChronoUnit.DAYS)),
				lot("Dune first edition", "1965 Chilton, first issue dust jacket, no remainder mark.",
						"https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=1400&q=80",
						"Books", "Used - Very Good", "London", 42000, 1000, now.plus(12, ChronoUnit.HOURS)),
				lot("Nike Dunk Low 1985", "Original high-top pairing, size US 9, stored boxed.",
						"https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1400&q=80",
						"Sneakers", "Used - Fair", "Tokyo", 15000, 500, now.plus(3, ChronoUnit.DAYS)),
				lot("Eames lounge chair", "670/671, walnut and black leather, recent webbing.",
						"https://images.unsplash.com/photo-1555041469-a586c61ea9bc?auto=format&fit=crop&w=1400&q=80",
						"Furniture", "Used - Excellent", "Los Angeles", 95000, 2500, now.plus(5, ChronoUnit.DAYS)));
		auctions.saveAll(lots);
	}

	private static Auction lot(
			String title,
			String description,
			String imageUrl,
			String category,
			String condition,
			String location,
			long start,
			long increment,
			Instant endsAt) {
		Auction auction = new Auction();
		auction.setSellerClerkUserId(SELLER);
		auction.setTitle(title);
		auction.setDescription(description);
		auction.setImageUrl(imageUrl);
		auction.setCategory(category);
		auction.setCondition(condition);
		auction.setLocation(location);
		auction.setStartPriceCents(start);
		auction.setMinIncrementCents(increment);
		auction.setCurrentPriceCents(start);
		auction.setStatus(AuctionStatus.OPEN);
		auction.setEndsAt(endsAt);
		return auction;
	}
}
