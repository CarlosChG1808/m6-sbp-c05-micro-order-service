package com.example.order_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
//@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    public Product getProductById(Long productId) {

        //String url = "http://localhost:8082/api/products/" + productId;
        String url = productServiceUrl + "/api/products/" + productId;
        Product product = restTemplate.getForObject(url, Product.class);
        log.info("Product retrievied successfully from productdb: {}", product);
        return product;
    }
        private Product getProductByIdFallback(Long productId, Throwable throwable) {
            log.warn("Fallback method invoked for getProductById due to: {}", throwable.getMessage());
            return Product.builder()
                    .id(productId)
                    .name("Unknown Product")
                    .description("Product information not available")
                    .price(BigDecimal.ZERO)
                    .stock(0)
                    .build();
        }
}
