package com.bidding.paymentservice.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bidding.paymentservice.domain.Wallet;

import jakarta.persistence.LockModeType;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

	Optional<Wallet> findByClerkUserId(String clerkUserId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select w from Wallet w where w.clerkUserId = :clerkUserId")
	Optional<Wallet> findByClerkUserIdForUpdate(@Param("clerkUserId") String clerkUserId);
}
