package com.example.iclabs.users;


import com.example.iclabs.dto.request.RequestRegisUserDTO;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc
@SpringBootTest
public class UserRegisTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void usersRegisSuccess() throws Exception {
        RequestRegisUserDTO regisUserDTO = new RequestRegisUserDTO();


        regisUserDTO.setName("Ahmad Rendi");
        regisUserDTO.setPass("@hmaD21");
        regisUserDTO.setNim("13020210048");

        mockMvc.perform(
                post("/api/v1/auth/users/regis")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regisUserDTO))
        ).andExpect(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> userDTO = mapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );

                    Assertions.assertNotNull(userDTO.data());
                    Assertions.assertNull(userDTO.error());
                    Assertions.assertEquals(userDTO.message(), "Berhasil Menambahkan");
                    Assertions.assertEquals(userDTO.code(), HttpStatus.CREATED.value());


                    System.out.println("message : " + userDTO.message());
                    System.out.println("code : " + userDTO.code());
                }
        );
    }
}
