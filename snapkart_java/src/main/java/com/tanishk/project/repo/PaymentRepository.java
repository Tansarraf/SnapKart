package com.tanishk.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tanishk.project.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

}
