package org.example.schoolback.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.schoolback.dto.request.MakeOrderRequest;
import org.example.schoolback.entity.*;
import org.example.schoolback.repository.OrderRepository;
import org.example.schoolback.repository.PresentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PresentService presentService;

    public Optional<Order> getById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Page<Order> getByCustomerId(Long customerId, Pageable pageable) {
        final Optional<User> customer = userService.getUserById(customerId);
        if (customer.isEmpty()) {
            throw new IllegalArgumentException(String.format("Customer with id %s not found", customerId));
        }

        return orderRepository.findAllByCustomer(customer.get(), pageable);
    }

    public Page<Order> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order makeOrder(Long customerId, Long presentId) {
        final Optional<User> customer = userService.getUserById(customerId);
        if (customer.isEmpty()) {
            throw new IllegalArgumentException(String.format("User with id %s not found", customerId));
        }

        final Present present = presentService.getPresentById(presentId);

        if (customer.get().getCoins() - present.getPriceCoins() < 0) {
            throw new RuntimeException("User does not have enough coins to buy present");
        }
        if (present.getStock() <= 0) {
            throw new RuntimeException("It is impossible to place an order, as there are no presents left.");
        }

        final Order order = new Order();
        order.setCustomer(customer.get());
        order.setPresent(present);
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderDate(LocalDateTime.now());
        validateCreate(order);

        userService.setCoinsForUser(customer.get().getId(), customer.get().getCoins() - present.getPriceCoins());
        presentService.setStock(present.getId(), present.getStock() - 1);
        return orderRepository.save(order);
    }

    public Order setStatusForOrder(Long orderId, OrderStatus status) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new IllegalArgumentException(String.format("Order with id %s not found", orderId));
        }
        if (order.get().getStatus().getPriority() + 1 != status.getPriority()) {
            throw new IllegalArgumentException("Incorrect order status change procedure");
        }

        order.get().setStatus(status);

        return orderRepository.save(order.get());
    }


    private void validateCreate(Order order) {
        validateCommonFields(order);
    }


    protected void validateUpdate(Long orderId, Order updatedOrder) {
        validateCommonFields(updatedOrder);

        final Order existOrder = orderRepository.findById(orderId).get();

        if (!existOrder.getCustomer().getId().equals(updatedOrder.getCustomer().getId())) {
            throw new IllegalArgumentException("Customer of order cannot be changed");
        }

        if (!existOrder.getPresent().getId().equals(updatedOrder.getPresent().getId())) {
            throw new IllegalArgumentException("Present of order cannot be changed");
        }
    }

    private void validateCommonFields(Order order) {
        if (order.getCustomer() == null) {
            throw new IllegalArgumentException("Order must have a customer");
        } else if (!order.getCustomer().getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("The customer can only be a student");
        }

        if (order.getPresent() == null) {
            throw new IllegalArgumentException("Order must have a present");
        }

        if (order.getOrderDate() == null) {
            throw new IllegalArgumentException("Order must have an order date");
        }

        if (order.getStatus() == null) {
            throw new IllegalArgumentException("Order must have a status");
        }
    }
}
