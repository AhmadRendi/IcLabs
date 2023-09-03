package com.example.iclabs.service;

import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;
import com.example.iclabs.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getUserByNim() {
        String nim = "13020210050";

        User user = userService.getUserByNimCache(nim);

        System.out.println("nim : " + user);
    }

    @Test
    void getUserByNimCache() throws Exception {
        mockMvc.perform(
                get("/api/v1/auth/nim/13020210050")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isFound()
        ).andDo( result -> {
            ResponseAPI<?> responseAPI = objectMapper.readValue(
                    result.getResponse()
                            .getContentAsString()
                    , ResponseAPI.class
            );
            Assertions.assertNotNull(responseAPI.data());
            Assertions.assertEquals("berhasil ditemukan", responseAPI.message());
            Assertions.assertEquals(302, responseAPI.code());
            Assertions.assertNull(responseAPI.error());

            System.out.println("Response Code : " + responseAPI.code());
//            System.out.println("Response data : " + responseAPI.data());
            System.out.println("Response message : " + responseAPI.message());
        });
    }
}
