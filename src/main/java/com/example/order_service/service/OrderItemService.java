package com.example.order_service.service;

import com.example.order_service.client.Product;
import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderItem;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.mapper.OrderItemMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor

public class OrderItemService {

    private final OrderItemMapper orderItemMapper;
    private final ProductClient productClient;

    public List<OrderItem> buildOrderItemsWithProducts(List<OrderItemEntity> itemEntities) {

        return itemEntities.stream()
                .map(itemEntity -> {
                    Product product = productClient.getProductById(itemEntity.getProductId());
                    return orderItemMapper.toDomainWhithProduct(itemEntity, product);
                })
                .collect(Collectors.toList());
    }

    public Product getProductById(Long productId) {
        log.info("Getting product by id: {}", productId);
        return productClient.getProductById(productId);
    }
}
