package org.example.schoolback.repository;

import org.example.schoolback.entity.Order;
import org.example.schoolback.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByCustomerId(Long customerId, Pageable pageable);

    Page<Order> findAllByCustomer(User customer, Pageable pageable);
}
