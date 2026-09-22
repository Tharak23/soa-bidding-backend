package com.bidding.paymentservice.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.paymentservice.domain.LedgerEntry;
import com.bidding.paymentservice.service.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

	private final WalletService wallets;

	public WalletController(WalletService wallets) {
		this.wallets = wallets;
	}

	@GetMapping
	public WalletResponse get() {
		return wallets.myWallet();
	}

	@GetMapping("/ledger")
	public List<LedgerEntry> ledger() {
		return wallets.myLedger();
	}

	@PostMapping("/top-up")
	public CheckoutResponse topUp(@Valid @RequestBody TopUpRequest request) {
		return wallets.startTopUp(request.amountCents());
	}

	@PostMapping("/sync")
	public WalletResponse sync() {
		return wallets.syncOpenTopUps();
	}
}
