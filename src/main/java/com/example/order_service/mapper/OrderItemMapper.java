package com.example.order_service.mapper;

import com.example.order_service.client.Product;
import com.example.order_service.dto.OrderItem;
import com.example.order_service.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItem toDomain(OrderItemEntity orderItemEntity);

    default OrderItem toDomainWhithProduct(OrderItemEntity orderItemEntity, Product product) {
        OrderItem orderItem = toDomain(orderItemEntity);
        orderItem.setProduct(product);
        return orderItem;
    }


}
