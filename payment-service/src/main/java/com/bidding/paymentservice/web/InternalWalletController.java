package com.bidding.paymentservice.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.paymentservice.service.WalletService;

@RestController
@RequestMapping("/internal/wallet")
public class InternalWalletController {

	private final WalletService wallets;

	public InternalWalletController(WalletService wallets) {
		this.wallets = wallets;
	}

	@PostMapping("/hold")
	public void hold(@RequestBody HoldRequest request) {
		wallets.hold(request);
	}

	@PostMapping("/release")
	public void release(@RequestBody ReleaseRequest request) {
		wallets.release(request);
	}

	@PostMapping("/settle")
	public void settle(@RequestBody SettleRequest request) {
		wallets.settle(request);
	}
}
