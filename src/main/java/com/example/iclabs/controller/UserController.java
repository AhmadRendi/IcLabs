package com.example.iclabs.controller;

import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.request.UpdateName;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;
import com.example.iclabs.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @Deprecated
    @PostMapping("/register")
    public ResponseEntity<ResponseAPI<?>> register(
            @Valid
            @ModelAttribute RegisterDTO request,
            Errors errors
    ) throws IOException {
        return ResponseEntity.ok(
                userService.registrasi(request, errors)
        );
    }

    @GetMapping("/login")
    public ResponseEntity<AuthReponse> authentication (
            @RequestBody LoginDTO request
    ){
        return ResponseEntity.ok(
                userService.login(request)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseAPI<?>> updateNameUser(@Valid @RequestBody  UpdateName updateName,
                                                         Errors errors
    ){
        return ResponseEntity.ok(
                userService.updateNameUser(
<<<<<<< HEAD
                        updateName, errors)
=======
                        updateName, updateName.getNim(), errors)
>>>>>>> 2fb10281cee9f45fa367f2f5f71ed1148e3e380f
        );
    }

}
