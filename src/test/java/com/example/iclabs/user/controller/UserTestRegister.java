package com.example.iclabs.user.controller;

import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.io.*;
import java.util.InputMismatchException;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Deprecated
public class UserTestRegister {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final static String aut = "Authorization";

    private final static String bear = "Bearer ";

    @Test
    void registerSuccess() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File("/home/ahmad/Pictures/foto saya/index1.jpeg");
        MockMultipartFile mockImgeFile = null;
        try(FileInputStream inputImage = new FileInputStream(fileImage)){
            mockImgeFile =  new MockMultipartFile("image",
                    fileImage.getName(),
                    "multipart/form-data",
                    inputImage
            );
        }catch (IOException e){
            Assertions.assertNull(e);
        }

        File fileCv = new File("/home/ahmad/Pictures/foto saya/index1.jpeg");
        MockMultipartFile mockCvFile = null;
        try(FileInputStream inputCv = new FileInputStream(fileCv)){
            mockCvFile = new MockMultipartFile("cv",
                    fileCv.getName(),
                    "multipart/form-data",
                    inputCv
            );
        }catch (IOException e){
            Assertions.assertNull(e);
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210053");
        registerDTO.setPass("bi@ncA09");
        registerDTO.setNameMateri("Kotlin");

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
                    System.out.println("Code message : " + responseAPI.message());
                }
        );
    }


    String login() throws Exception {

        String nim = "13020210053";
        String pass = "bi@ncA09";

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
    void registerFailedBecauseNameIsNull() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File("D:\\album\\asser1\\WhatsApp " +
                "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){

             mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipart/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new IOException(exception.getMessage());
        }

        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipart/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new IOException(exception.getMessage());
        }

        registerDTO.setName("");
        registerDTO.setNim("13020210051");
        registerDTO.setPass("@hmadRendi21");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
          result -> {
              ResponseAPI<?> responseAPI = objectMapper.readValue(
                      result.getResponse().getContentAsString(), ResponseAPI.class
              );
              Assertions.assertEquals("gagal menambahkan", responseAPI.message());
              Assertions.assertNotNull(responseAPI.error());
              Assertions.assertEquals(500, responseAPI.code());

              System.out.println("status code : " + responseAPI.code());
              System.out.println("message : " + responseAPI.message());
              System.out.println("message error : " + responseAPI.error());
          }
        );
    }

    @Test
    void registerIsFailedBecauseNimIsNull() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("");
        registerDTO.setPass("@hmadRendi21");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }


    @Test
    void registerIsFailedBecausePassIsNull() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210052");
        registerDTO.setPass("");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }


    @Test
    void registerIsFailedBecauseNameMateriIsNull() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210052");
        registerDTO.setPass("@hmadRendi21");
        registerDTO.setNameMateri("");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }

    @Test
    void registerIsFailedBecausePasswordDontHaveUpCase() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210052");
        registerDTO.setPass("bi@nca9");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }


    @Test
    void registerIsFailedBecausePasswordDontHaveLowCase() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210052");
        registerDTO.setPass("BI@NCA9");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }

    @Test
    void registerIsFailedBecausePasswordDontHaveSymbol() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210052");
        registerDTO.setPass("biAnca9");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }

    @Test
    void registerIsFailedBecausePasswordDontHaveNumber() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO();

        File fileImage = new File(
                "D:\\album\\asser1\\WhatsApp " +
                        "Image 2023-08-30 at 10.33.32.jpg"
        );
        MockMultipartFile mockFileImage = null;
        try(FileInputStream inputFileImage = new FileInputStream(fileImage)){
            mockFileImage = new MockMultipartFile(
                    "image", fileImage.getName(),
                    "multipartfile/form-data", inputFileImage
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }


        File fileCv = new File(
                "D:\\album\\asser1\\Screenshot 2023-08-20 005201.png"
        );
        MockMultipartFile mockFileCv = null;
        try(FileInputStream inputFileCv = new FileInputStream(fileCv)){
            mockFileCv = new MockMultipartFile(
                    "cv", fileCv.getName(),
                    "multipartfile/form-data", inputFileCv
            );
        }catch (IOException exception){
            throw new InputMismatchException(exception.getMessage());
        }

        registerDTO.setName("Afian Febrianto");
        registerDTO.setNim("13020210052");
        registerDTO.setPass("bi@ncA");
        registerDTO.setNameMateri("Kotlin");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/auth/register")
                        .file(mockFileCv)
                        .file(mockFileImage)
                        .param("name", registerDTO.getName())
                        .param("nim", registerDTO.getNim())
                        .param("pass", registerDTO.getPass())
                        .param("nameMateri", registerDTO.getNameMateri())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertEquals("gagal menambahkan", responseAPI.message());
                    Assertions.assertNotNull(responseAPI.error());
                    Assertions.assertEquals(500, responseAPI.code());

                    System.out.println("status code : " + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                    System.out.println("message error : " + responseAPI.error());
                }
        );
    }

    @Test
    void getUserByNimCache() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/nim/13020210050")
                .contentType(MediaType.APPLICATION_JSON)
                .header(aut, bear + login())
        ).andExpectAll(
                status().isFound()
        ).andDo(
                result -> {
                    ResponseAPI<?> responseAPI = objectMapper.readValue(
                            result.getResponse().getContentAsString(), ResponseAPI.class
                    );
                    Assertions.assertNotNull(responseAPI.data());
                    Assertions.assertEquals("berhasil ditemukan", responseAPI.message());
                    Assertions.assertNull(responseAPI.error());
                    Assertions.assertEquals(302, responseAPI.code());

                    System.out.println("code status :" + responseAPI.code());
                    System.out.println("message : " + responseAPI.message());
                }
        );
    }
}
