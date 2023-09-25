package com.example.iclabs.service;

import com.example.iclabs.dto.request.RequestRegisUserDTO;
import com.example.iclabs.dto.respons.ResponseAPI;
import org.springframework.validation.Errors;

public interface UsersService {

    public ResponseAPI<?> registration(RequestRegisUserDTO userDTO, Errors errors);
}
