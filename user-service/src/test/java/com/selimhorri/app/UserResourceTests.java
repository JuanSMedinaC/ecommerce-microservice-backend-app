// File: user-service/src/test/java/com/selimhorri/app/resource/UserResourceTest.java
package com.selimhorri.app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.resource.UserResource;
import com.selimhorri.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(UserResource.class)
public class UserResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void testFindById_ReturnsUser() throws Exception {
        when(userService.findById(1)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).findById(1);
    }

    @Test
    void testSaveUser_ReturnsCreatedUser() throws Exception {
        UserDto newUser = UserDto.builder()
                .userId(null)
                .firstName("Jane")
                .lastName("Smith")
                .build();
        UserDto savedUser = UserDto.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userService.save(any(UserDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(userService, times(1)).save(any(UserDto.class));
    }

    @Test
    void testFindByUsername_ReturnsUser() throws Exception {
        when(userService.findByUsername("johndoe")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/username/johndoe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).findByUsername("johndoe");
    }
}