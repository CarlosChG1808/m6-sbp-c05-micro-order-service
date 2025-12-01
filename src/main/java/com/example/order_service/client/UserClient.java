package com.example.order_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
//@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public User getUserById(Long createdBy) {

        //String url = "http://localhost:8080/api/users/" + createdBy;
        String url = userServiceUrl + "/api/users/" + createdBy;
        User usr = restTemplate.getForObject(url, User.class);
        log.info("User retrievied successfully from userdb: {}", usr);
        return usr;
    }

    private User getUserByIdFallback(Long createdBy, Throwable throwable) {
        log.warn("Fallback method invoked for getUserById due to: {}", throwable.getMessage());
        return  User.builder()
                .id(createdBy)
                .name("Unknow User")
                .email("Unknow Email")
                .phone("Unknow Phone")
                .address("Unknow Address")
                .build();
    }
}
