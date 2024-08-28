package com.microservices.order.repository;

import com.microservices.order.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {

    public Page<Order> findAll(Pageable pageable);

}