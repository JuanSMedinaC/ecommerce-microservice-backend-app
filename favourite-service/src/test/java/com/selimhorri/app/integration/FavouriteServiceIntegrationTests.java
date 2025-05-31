package com.selimhorri.app.integration;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.repository.FavouriteRepository;
import com.selimhorri.app.service.impl.FavouriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class FavouriteServiceIntegrationTests {

    @Autowired
    private FavouriteServiceImpl favouriteService;

    @MockBean
    private FavouriteRepository favouriteRepository;

    @MockBean
    private RestTemplate restTemplate;

    private FavouriteDto favouriteDto;
    private FavouriteId favouriteId;
    private UserDto userDto;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        favouriteId = new FavouriteId(1, 2, LocalDateTime.now());
        favouriteDto = FavouriteDto.builder().userId(1).productId(2).build();
        userDto = UserDto.builder().userId(1).firstName("John").build();
        productDto = ProductDto.builder().productId(2).productTitle("Product").build();
    }

    @Test
    void findById_ReturnsFavouriteWithUserAndProduct() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.ofNullable(com.selimhorri.app.helper.FavouriteMappingHelper.map(favouriteDto)));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", UserDto.class)).thenReturn(userDto);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/2", ProductDto.class)).thenReturn(productDto);

        FavouriteDto result = favouriteService.findById(favouriteId);

        assertThat(result.getUserDto()).isEqualTo(userDto);
        assertThat(result.getProductDto()).isEqualTo(productDto);
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getProductId()).isEqualTo(2);
    }
}