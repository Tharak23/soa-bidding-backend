package com.bidding.paymentservice.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.paymentservice.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	Optional<Payment> findByDodoPaymentId(String dodoPaymentId);

	List<Payment> findByClerkUserIdAndStatus(String clerkUserId, String status);

	List<Payment> findByClerkUserIdOrderByCreatedAtDesc(String clerkUserId);
}
