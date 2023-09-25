package com.example.iclabs.controller;

import com.example.iclabs.dto.request.RequestRegisUserDTO;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.service.impl.UsersServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/users")
@AllArgsConstructor
public class CalonAsistenController {

    private UsersServiceImpl usersService;


    @PostMapping("/regis")
    public ResponseEntity<ResponseAPI<?>> registrasi(
            @Valid
            @RequestBody
            RequestRegisUserDTO userDTO,
            Errors errors){
        return ResponseEntity.ok(usersService.registration(userDTO, errors));
    }
}
