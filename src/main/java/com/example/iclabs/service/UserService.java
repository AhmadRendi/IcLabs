package com.example.iclabs.service;

import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;

import java.io.IOException;

public interface UserService {

    public ResponseAPI<?> registrasi(RegisterDTO registerDTO) throws IOException;

    public void updateNameUser(String name, Long id);

}
