package com.selimhorri.app.integration;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.CartDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderResourceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateOrder() {
        CartDto cart = CartDto.builder()
                .userId(1)
                .cartId(1)
                .build();

        OrderDto newOrder = OrderDto.builder()
                .orderDesc("Integration order")
                .orderFee(100.0)
                .cartDto(cart)
                .build();

        ResponseEntity<OrderDto> response = restTemplate.postForEntity("/api/orders", newOrder, OrderDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCartDto()).isNotNull();
        assertThat(response.getBody().getCartDto().getCartId()).isEqualTo(1);
    }
}