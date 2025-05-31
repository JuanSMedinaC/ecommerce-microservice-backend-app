
package com.selimhorri.app.unit;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.resource.OrderResource;
import com.selimhorri.app.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrderResourceTest {

    private OrderService orderService;
    private OrderResource orderResource;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderResource = new OrderResource(orderService);
    }

    @Test
    void findAll_ReturnsEmptyListWhenNoOrders() {
        when(orderService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<DtoCollectionResponse<OrderDto>> response = orderResource.findAll();

        assertThat(response.getBody().toString().isEmpty());
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void findById_WithValidId_ReturnsOrder() {
        OrderDto order = OrderDto.builder().orderDesc("Order").build();
        when(orderService.findById(1)).thenReturn(order);

        ResponseEntity<OrderDto> response = orderResource.findById("1");

        assertThat(response.getBody()).isEqualTo(order);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void save_WithValidOrder_ReturnsSavedOrder() {
        OrderDto order = OrderDto.builder().orderDesc("Order").build();
        when(orderService.save(order)).thenReturn(order);

        ResponseEntity<OrderDto> response = orderResource.save(order);

        assertThat(response.getBody()).isEqualTo(order);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void update_WithValidOrder_ReturnsUpdatedOrder() {
        OrderDto order = OrderDto.builder().orderDesc("Updated Order").build();
        when(orderService.update(order)).thenReturn(order);

        ResponseEntity<OrderDto> response = orderResource.update(order);

        assertThat(response.getBody()).isEqualTo(order);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void update_WithValidIdAndOrder_ReturnsUpdatedOrder() {
        OrderDto order = OrderDto.builder().orderDesc("Updated Order").build();
        when(orderService.update(1, order)).thenReturn(order);

        ResponseEntity<OrderDto> response = orderResource.update("1", order);

        assertThat(response.getBody()).isEqualTo(order);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

}