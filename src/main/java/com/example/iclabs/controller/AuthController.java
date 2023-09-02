package com.example.iclabs.controller;

import com.example.iclabs.auth.AuthenticationRequest;
import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.request.UpdateName;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;
import com.example.iclabs.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<AuthReponse> register(
            @RequestBody RegisterDTO request
    ){
        return ResponseEntity.ok(userService.registrasi(request));
    }

    @GetMapping("/login")
    public ResponseEntity<AuthReponse> authentication (
            @RequestBody LoginDTO request){
        return ResponseEntity.ok(userService.login(request));
    }


    @GetMapping("/get/all")
    public ResponseEntity<ResponseAPI<?>> getAll(){
        ResponseAPI<?> response = ResponseAPI.builder()
                .code(HttpStatus.FOUND.value())
                .message("berhasil")
                .data(userService.getAll()).build();

        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseAPI<?>> findById(@PathVariable Long id){
        User user = userService.getUserById(id);

        ResponseAPI<?> responseAPI = ResponseAPI.builder()
                .code(HttpStatus.FOUND.value())
                .message("ditemukan")
                .data(user)
                .build();
        return new ResponseEntity<>(responseAPI, HttpStatus.FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseAPI<?>> updateNameUser(@RequestBody  UpdateName updateName,
                                                         @PathVariable Long id){
        userService.updateName(updateName.getName(), id);
        userService.updateNameUser(updateName.getName(), id);
        ResponseAPI<?> responseAPI = ResponseAPI.builder()
                .code(HttpStatus.UPGRADE_REQUIRED.value())
                .message("berhasil")
                .build();
        return new ResponseEntity<>(responseAPI, HttpStatus.UPGRADE_REQUIRED);
    }
}
