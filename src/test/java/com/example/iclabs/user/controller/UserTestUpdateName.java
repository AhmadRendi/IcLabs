package com.example.iclabs.user.controller;

import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.UpdateName;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserTestUpdateName {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private final static String aut = "Authorization";

    private final static String bear = "Bearer ";


    String login() throws Exception {

        String nim = "13020210049";
        String pass = "123";

        AtomicReference<String> token = new AtomicReference<>("");

        LoginDTO loginDTO = new LoginDTO();

        loginDTO.setNim(nim);
        loginDTO.setPassword(pass);

        mockMvc.perform(
                get("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    AuthReponse authReponse = objectMapper.readValue(
                            result.getResponse().getContentAsString(), AuthReponse.class
                    );

                    token.set(authReponse.getToken());
                }
        );
        return token.get();
    }


    @Test
    void updateNameSuccess() throws Exception {
        UpdateName updateName = new UpdateName();

        updateName.setName("Fajri");
        updateName.setNim("13020210049");

        mockMvc.perform(
                put("/api/v1/auth/update")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateName))
                        .header(aut, bear + login())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );

                    Assertions.assertNull(responseAPI.error());
                    Assertions.assertEquals(201, responseAPI.code());
                    Assertions.assertEquals("berhasil merubah", responseAPI.message());


                    System.out.println("code status : "  + responseAPI.code());
                    System.out.println("message  : "  + responseAPI.message());
                }
        );
    }

    @Test
    void updateNameIsFailedBecauseNameIsNull() throws Exception {
        UpdateName updateName = new UpdateName();

        updateName.setName(" ");
        updateName.setNim("13020210049");

        mockMvc.perform(
                put("/api/v1/auth/update")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateName))
                        .header(aut, bear + login())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );

                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(304, responseAPI.code());
                    Assertions.assertEquals("gagal merubah", responseAPI.message());


                    System.out.println("code status : "  + responseAPI.code());
                    System.out.println("message  : "  + responseAPI.message());
                }
        );
    }
}
