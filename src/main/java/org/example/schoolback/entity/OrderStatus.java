package org.example.schoolback.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDERED(1),
    CONFIRMED(2),
    ISSUED(3),
    CANCELLED(2);

    private final int priority;

    OrderStatus(int priority) {
        this.priority = priority;
    }
}
