package com.selimhorri.app.integration;

import com.selimhorri.app.dto.UserDto;
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
public class UserResourceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetUserById() {
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/api/users/1", UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(1);
    }
}