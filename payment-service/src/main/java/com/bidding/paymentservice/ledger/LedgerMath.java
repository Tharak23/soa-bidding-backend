package com.bidding.paymentservice.ledger;

public final class LedgerMath {

	private LedgerMath() {
	}

	public static WalletState hold(WalletState state, long amountCents) {
		requirePositive(amountCents);
		if (state.availableCents() < amountCents) {
			throw new IllegalArgumentException("insufficient_funds");
		}
		return new WalletState(state.availableCents() - amountCents, state.heldCents() + amountCents);
	}

	public static WalletState release(WalletState state, long amountCents) {
		requirePositive(amountCents);
		if (state.heldCents() < amountCents) {
			throw new IllegalArgumentException("insufficient_hold");
		}
		return new WalletState(state.availableCents() + amountCents, state.heldCents() - amountCents);
	}

	public static WalletState capture(WalletState state, long amountCents) {
		requirePositive(amountCents);
		if (state.heldCents() < amountCents) {
			throw new IllegalArgumentException("insufficient_hold");
		}
		return new WalletState(state.availableCents(), state.heldCents() - amountCents);
	}

	public static WalletState topUp(WalletState state, long amountCents) {
		requirePositive(amountCents);
		return new WalletState(state.availableCents() + amountCents, state.heldCents());
	}

	private static void requirePositive(long amountCents) {
		if (amountCents <= 0) {
			throw new IllegalArgumentException("amount_must_be_positive");
		}
	}
}
