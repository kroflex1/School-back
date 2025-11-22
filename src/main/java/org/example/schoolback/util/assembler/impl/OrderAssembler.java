package org.example.schoolback.util.assembler.impl;

import org.example.schoolback.dto.OrderDTO;
import org.example.schoolback.entity.Order;
import org.example.schoolback.service.PresentService;
import org.example.schoolback.service.UserService;
import org.example.schoolback.util.assembler.ConverterModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAssembler extends ConverterModelAssembler<Order, OrderDTO> {
    @Autowired
    private UserResourceAssembler userResourceAssembler;
    @Autowired
    private UserService userService;
    @Autowired
    private PresentService presentService;

    @Override
    public OrderDTO toModel(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setDate(order.getOrderDate());
        dto.setPresentId(order.getPresent().getId());
        dto.setCustomer(userResourceAssembler.toModel(order.getCustomer()));
        dto.setStatus(order.getStatus());

        return dto;
    }

    @Override
    public Order createEntity(OrderDTO orderDTO) {
        Order order = new Order();
        updateEntity(order, orderDTO);
        return order;
    }

    @Override
    public void updateEntity(Order existingEntity, OrderDTO resource) {
        existingEntity.setOrderDate(resource.getDate());
        existingEntity.setStatus(resource.getStatus());
        existingEntity.setCustomer(userService.getUserById(resource.getCustomer().getId()).get());
        existingEntity.setPresent(presentService.getPresentById(resource.getPresentId()));
    }
}
