package com.dilaraalk.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.order.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

}
