package com.selimhorri.app.integration;

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.CategoryDto;
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
public class ProductResourceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateProduct() {
        CategoryDto category = CategoryDto.builder()
                .categoryId(1) // Use a valid categoryId from your test DB
                .categoryTitle("Default Category")
                .build();

        ProductDto newProduct = ProductDto.builder()
                .productTitle("Integration Product")
                .sku("INT-SKU-001")
                .priceUnit(50.0)
                .quantity(20)
                .categoryDto(category)
                .imageUrl("http://example.com/image.jpg")
                .build();

        ResponseEntity<ProductDto> response = restTemplate.postForEntity("/api/products", newProduct, ProductDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProductTitle()).isEqualTo("Integration Product");
    }
}