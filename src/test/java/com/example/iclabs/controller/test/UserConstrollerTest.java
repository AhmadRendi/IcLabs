package com.example.iclabs.controller.test;

import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.service.impl.UserServiceImpl;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserConstrollerTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    static class MockMultipartFileSerializer extends JsonSerializer<MockMultipartFile>{
        @Override
        public void serialize(MockMultipartFile value
                , JsonGenerator gen
                , SerializerProvider serializers) throws IOException {

            gen.writeStartObject();
            gen.writeStringField("originalFileName", value.getOriginalFilename());
            gen.writeStringField("contentType", value.getContentType());
            gen.writeStringField("fileSize", String.valueOf(value.getSize()));

            gen.writeEndObject();
        }
    }

    static interface Regis <T, R> {

    }

    static class RegisterSerializer extends JsonSerializer<RegisterDTO>{

        @Override
        public void serialize(RegisterDTO value,
                              JsonGenerator gen,
                              SerializerProvider serializers) throws IOException {
            gen.writeStartObject();

            gen.writeStringField("name", value.getName());
            gen.writeStringField("nim", value.getNim());
            gen.writeStringField("pass", value.getPass());
            gen.writeStringField("image", value.getImage().getOriginalFilename());
            gen.writeStringField("nameMateri", value.getNameMateri());
            gen.writeStringField("cv", value.getCv().getOriginalFilename());

            gen.writeEndObject();
        }
    }

    static  class Costom {
        public ObjectMapper customObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(MockMultipartFile.class, new MockMultipartFileSerializer());
            objectMapper.registerModule(module);
            return objectMapper;
        }
    }

    static  class CostomRegister{
        public ObjectMapper customObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(RegisterDTO.class, new RegisterSerializer());
            objectMapper.registerModule(module);
            return objectMapper;
        }
    }

    @Test
    void testName() {
        File file = new File("D:\\album\\asser1\\WhatsApp Image 2023-08-30 at 10.33.32.jpg");
        System.out.println(file.getName());
    }

    @Test
    void registerSuccess() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();



        File fileImage = new File("D:\\album\\asser1\\WhatsApp " +
                "Image 2023-08-30 at 10.33.32.jpg");
        MockMultipartFile mockImgeFile = null;
        try(FileInputStream inputImage = new FileInputStream(fileImage)){
            mockImgeFile =  new MockMultipartFile("image",
                    fileImage.getName(),
                    "multipart/form-data",
                    inputImage
            );
            inputImage.close();
        }catch (IOException e){
            Assertions.assertNull(e);
        }


        File fileCv = new File("D:\\album\\asser1\\Screenshot 2023-08-20 005201.png");
        MockMultipartFile mockCvFile = null;
        try(FileInputStream inputCv = new FileInputStream(fileCv)){
            mockCvFile = new MockMultipartFile("cv",
                    fileCv.getName(),
                    "multipart/form-data",
                    inputCv
            );
            inputCv.close();
        }catch (IOException e){
            Assertions.assertNull(e);
        }
        registerDTO.setName("Nasrullah");
        registerDTO.setNim("12093284924");
        registerDTO.setPass("123");
        registerDTO.setNameMateri("Mecine Learning");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                .file(mockImgeFile)
                .file(mockCvFile)
                .param("name", registerDTO.getName())
                .param("nim", registerDTO.getNim())
                .param("pass", registerDTO.getPass())
                .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper
                            .readValue(result.getResponse().getContentAsString(), ResponseAPI.class);

                    Assertions.assertNull(responseAPI.data());
                    Assertions.assertEquals(201, responseAPI.code());
                    Assertions.assertEquals("berhasil menambahkan", responseAPI.message());
                    Assertions.assertNull(responseAPI.error());

                    System.out.println("Code status : " + responseAPI.code());
                    System.out.println("Data " + responseAPI.data());
                    System.out.println("Code message : " + responseAPI.message());
                }
        );
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
