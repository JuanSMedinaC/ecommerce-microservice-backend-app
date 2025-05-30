package com.selimhorri.app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.resource.ProductResource;
import com.selimhorri.app.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto testProduct;

    @BeforeEach
    void setUp() {
        testProduct = ProductDto.builder()
                .productId(1)
                .productTitle("Test Product")
                .imageUrl("http://example.com/image.jpg")
                .sku("TEST-SKU-123")
                .priceUnit(99.99)
                .quantity(10)
                .build();
    }

    @Test
    void testFindById_ReturnsProduct() throws Exception {
        when(productService.findById(1)).thenReturn(testProduct);

        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productTitle").value("Test Product"))
                .andExpect(jsonPath("$.sku").value("TEST-SKU-123"))
                .andExpect(jsonPath("$.priceUnit").value(99.99))
                .andExpect(jsonPath("$.quantity").value(10));

        verify(productService, times(1)).findById(1);
    }

    @Test
    void testSaveProduct_ReturnsCreatedProduct() throws Exception {
        ProductDto newProduct = ProductDto.builder()
                .productId(null)
                .productTitle("New Product")
                .imageUrl("http://example.com/new-image.jpg")
                .sku("NEW-SKU-456")
                .priceUnit(149.99)
                .quantity(5)
                .build();

        ProductDto savedProduct = ProductDto.builder()
                .productId(2)
                .productTitle("New Product")
                .imageUrl("http://example.com/new-image.jpg")
                .sku("NEW-SKU-456")
                .priceUnit(149.99)
                .quantity(5)
                .build();

        when(productService.save(any(ProductDto.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.productTitle").value("New Product"))
                .andExpect(jsonPath("$.sku").value("NEW-SKU-456"))
                .andExpect(jsonPath("$.priceUnit").value(149.99))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(productService, times(1)).save(any(ProductDto.class));
    }
}