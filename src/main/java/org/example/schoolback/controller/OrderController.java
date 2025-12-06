package org.example.schoolback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.schoolback.dto.OrderDTO;
import org.example.schoolback.dto.request.MakeOrderRequest;
import org.example.schoolback.entity.Order;
import org.example.schoolback.entity.OrderStatus;
import org.example.schoolback.entity.Role;
import org.example.schoolback.entity.User;
import org.example.schoolback.service.OrderService;
import org.example.schoolback.service.UserService;
import org.example.schoolback.util.assembler.impl.OrderAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderAssembler orderAssembler;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Получить информацию о заказе по его id")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderService.getById(id);
        if (order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OrderDTO resource = orderAssembler.toModel(order.get());
        return ResponseEntity.ok(resource);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN',  'STUDENT')")
    @Operation(summary = "Получить информацию о заказах",
            description = """
                    Получить заказы, которые доступны пользователю.
                    По умолчанию сортируется по дате создания заказа.
                    
                    **Влияние роли:**
                    - ADMIN: получает информацию о всех заказах;
                    - STUDENT: получает информацию только о своих заказах;
                    """)
    public Page<OrderDTO> getAvailableOrders(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        final User currentUser = userService.getCurrentUser().get();
        Page<Order> result;
        if (currentUser.getRole().equals(Role.STUDENT)) {
            result = orderService.getByCustomerId(currentUser.getId(), pageable);
        } else {
            result = orderService.getAll(pageable);
        }
        return result.map(orderEntity -> orderAssembler.toModel(orderEntity));
    }


    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Сделать зазказ подарка", description = "Сделать заказ может только студент")
    public ResponseEntity<OrderDTO> makeOrder(@RequestParam Long presentId) {
        final User currentUser = userService.getCurrentUser().get();
        final Order order = orderService.makeOrder(currentUser.getId(), presentId);
        final OrderDTO resource = orderAssembler.toModel(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус заказа")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        final Order updatedOrder = orderService.setStatusForOrder(id, status);
        final OrderDTO resource = orderAssembler.toModel(updatedOrder);
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @Operation(summary = "Отменить заказ")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
        final Order updatedOrder = orderService.setStatusForOrder(id, OrderStatus.CANCELLED);
        final OrderDTO resource = orderAssembler.toModel(updatedOrder);
        return ResponseEntity.ok(resource);
    }
}
