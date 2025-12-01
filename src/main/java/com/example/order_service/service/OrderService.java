package com.example.order_service.service;

import com.example.order_service.client.Product;
import com.example.order_service.client.ProductClient;
import com.example.order_service.client.User;
import com.example.order_service.client.UserClient;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.Order;
import com.example.order_service.dto.OrderItem;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.mapper.OrderItemMapper;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient  userClient;
    private final OrderItemService  orderItemService;

    public Order getOrderById(Long id) {

        OrderEntity orderEntity = orderRepository.findById(id).orElse(null);

        User user = userClient.getUserById(orderEntity.getUserId());
        log.info("User: {}", user.getName());

        List<OrderItem> items = orderItemService.buildOrderItemsWithProducts(orderEntity.getItems());

        return orderMapper.toDomainWithUserAndItems(orderEntity, items, user);
    }

    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        User user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }

        String orderNumber = generateOrderNumber();

        List<OrderItemEntity> itemEntities = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = orderItemService.getProductById(itemRequest.getProductId());

                    if (product == null) {
                        throw new RuntimeException("Product not found with id: " + itemRequest.getProductId());
                    }

                    BigDecimal unitPrice = product.getPrice();
                    BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                    return OrderItemEntity.builder()
                            .productId(itemRequest.getProductId())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(unitPrice)
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        BigDecimal totalAmount = itemEntities.stream()
                .map(OrderItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity orderEntity = OrderEntity.builder()
                .orderNumber(orderNumber)
                .userId(request.getUserId())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .items(itemEntities)
                .build();

        itemEntities.forEach(item -> item.setOrder(orderEntity));

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        log.info("Order created successfully: {}", savedOrder.getOrderNumber());

        List<OrderItem> itemsDto = orderItemService.buildOrderItemsWithProducts(savedOrder.getItems());

        return orderMapper.toDomainWithUserAndItems(savedOrder, itemsDto, user);
    }

    private String generateOrderNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();;
        return "ORD-" + year + "-" + uuid;
    }
}

