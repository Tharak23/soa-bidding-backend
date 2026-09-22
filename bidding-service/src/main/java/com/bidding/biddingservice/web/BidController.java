package com.bidding.biddingservice.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.biddingservice.service.BiddingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bids")
public class BidController {

	private final BiddingService bidding;

	public BidController(BiddingService bidding) {
		this.bidding = bidding;
	}

	@PostMapping
	public BidResponse place(@Valid @RequestBody PlaceBidRequest request) {
		return bidding.place(request);
	}

	@GetMapping("/me")
	public List<BidResponse> mine() {
		return bidding.mine();
	}
}
