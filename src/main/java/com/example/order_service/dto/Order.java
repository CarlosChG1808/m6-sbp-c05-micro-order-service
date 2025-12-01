package com.example.order_service.dto;

import com.example.order_service.client.User;
import com.example.order_service.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({ 
        "id",
        "orderNumber",
        "user",
        "items",
        "totalAmount",
        "status",
        "createdAt",
        "updatedAt"
})
public class Order {
    private Long id;
    private String orderNumber;

    @JsonProperty("user")
    private User createdbyUser;

    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
