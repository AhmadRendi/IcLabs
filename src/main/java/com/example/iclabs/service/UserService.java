package com.example.iclabs.service;

import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.request.UpdateName;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;
import org.springframework.validation.Errors;

import java.io.IOException;

@Deprecated
public interface UserService {

    public ResponseAPI<?> registrasi(RegisterDTO registerDTO, Errors errors) throws IOException;

    public ResponseAPI<?> updateNameUser(UpdateName name, Errors errors);
    public ResponseAPI<?> updateNameUser(UpdateName name, String nim, Errors errors);

    public User findByNim(String nim);

}
