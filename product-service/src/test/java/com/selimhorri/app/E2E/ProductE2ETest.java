package com.selimhorri.app.E2E;

import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    private CategoryDto buildCategory() {
        return CategoryDto.builder()
                .categoryId(1) // Use a valid categoryId from your test DB
                .categoryTitle("Default Category")
                .build();
    }

    private ProductDto buildProduct() {
        return ProductDto.builder()
                .productTitle("Book")
                .priceUnit(19.99)
                .sku("ASD-123")
                .imageUrl("example.com")
                .quantity(2)
                .categoryDto(buildCategory())
                .build();
    }

    @Test
    void createProduct_Success() {
        ProductDto product = buildProduct();
        ResponseEntity<ProductDto> response = restTemplate.postForEntity("/api/products", product, ProductDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals("Book", response.getBody().getProductTitle());
    }

    @Test
    void getProductById_Success() {
        ProductDto created = restTemplate.postForEntity("/api/products", buildProduct(), ProductDto.class).getBody();
        assertNotNull(created);
        ProductDto fetched = restTemplate.getForObject("/api/products/" + created.getProductId(), ProductDto.class);
        assertEquals(created.getProductId(), fetched.getProductId());
    }

    @Test
    void updateProduct_Success() {
        ProductDto created = restTemplate.postForEntity("/api/products", buildProduct(), ProductDto.class).getBody();
        assertNotNull(created);

        // Ensure all required fields are set
        ProductDto updateDto = ProductDto.builder()
                .productId(created.getProductId())
                .productTitle("Updated Book")
                .priceUnit(created.getPriceUnit())
                .sku(created.getSku())
                .imageUrl(created.getImageUrl())
                .quantity(created.getQuantity())
                .categoryDto(created.getCategoryDto())
                .build();

        // Use the base endpoint instead of the path variable endpoint
        restTemplate.put("/api/products", updateDto);

        ProductDto updated = restTemplate.getForObject("/api/products/" + created.getProductId(), ProductDto.class);
        assertEquals("Updated Book", updated.getProductTitle());
    }

    @Test
    void deleteProduct_Success() {
        // Create a product first
        ProductDto created = restTemplate.postForEntity("/api/products", buildProduct(), ProductDto.class).getBody();
        assertNotNull(created);
        Integer productId = created.getProductId();

        // Verify it exists before deletion
        ResponseEntity<ProductDto> beforeDelete = restTemplate.getForEntity("/api/products/" + productId, ProductDto.class);
        assertEquals(HttpStatus.OK, beforeDelete.getStatusCode());

        // Delete the product
        ResponseEntity<Boolean> deleteResponse = restTemplate.exchange(
                "/api/products/" + productId,
                HttpMethod.DELETE,
                null,
                Boolean.class);

        // Verify the delete operation returned success
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(Boolean.TRUE.equals(deleteResponse.getBody()));
    }

    @Test
    void listProducts_Success() {
        restTemplate.postForEntity("/api/products", buildProduct(), ProductDto.class);
        ResponseEntity<String> response = restTemplate.getForEntity("/api/products", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Book");
    }
}