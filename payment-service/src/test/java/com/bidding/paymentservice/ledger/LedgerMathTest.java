package com.bidding.paymentservice.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LedgerMathTest {

	@Test
	void holdMovesAvailableToHeld() {
		WalletState next = LedgerMath.hold(new WalletState(1000, 0), 400);
		assertEquals(600, next.availableCents());
		assertEquals(400, next.heldCents());
	}

	@Test
	void holdRejectsInsufficientFunds() {
		assertThrows(IllegalArgumentException.class, () -> LedgerMath.hold(new WalletState(100, 0), 200));
	}

	@Test
	void releaseReturnsHoldToAvailable() {
		WalletState next = LedgerMath.release(new WalletState(100, 400), 400);
		assertEquals(500, next.availableCents());
		assertEquals(0, next.heldCents());
	}

	@Test
	void captureRemovesHoldWithoutRefund() {
		WalletState next = LedgerMath.capture(new WalletState(100, 400), 400);
		assertEquals(100, next.availableCents());
		assertEquals(0, next.heldCents());
	}

	@Test
	void topUpCreditsAvailable() {
		assertEquals(1500, LedgerMath.topUp(new WalletState(500, 0), 1000).availableCents());
	}

	@Test
	void duplicateCreditIsJustAnotherTopUpCallerMustGuard() {
		WalletState first = LedgerMath.topUp(new WalletState(0, 0), 1000);
		WalletState second = LedgerMath.topUp(first, 1000);
		assertEquals(2000, second.availableCents());
	}
}
