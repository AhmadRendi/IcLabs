package com.example.iclabs.service.impl;

import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.request.UpdateName;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;
import com.example.iclabs.repo.UserRepo;
import com.example.iclabs.role.Role;
import com.example.iclabs.security.jwt.JWTService;
import com.example.iclabs.service.UserService;
import com.example.iclabs.validation.DoubleNimException;
import com.example.iclabs.validation.ErrorHandling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;


import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
@org.springframework.cache.annotation.CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService service;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByNim(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("nim not found");
        });
    }

    private boolean doubleNim(String nim){
        if(findByNim(nim) != null){
            throw new DoubleNimException("nim telah digunakan");
        }
        return true;
    }


    private boolean cekPassword(String password){

        boolean findUpCaseInPassword = Pattern.compile("[A-Z]").matcher(password).find();
        boolean findNumberInPassword = Pattern.compile("[\\d]").matcher(password).find();
        boolean findSymbolInPassword = Pattern.compile("[\\W]").matcher(password).find();
        boolean findLowCaseInPassword = Pattern.compile("[a-z]").matcher(password).find();

        if(
                findSymbolInPassword &&
                        findUpCaseInPassword &&
                        findNumberInPassword &&
                        findLowCaseInPassword
        ){
            return true;
        }
        throw new InputMismatchException(
                "password harus campuran angka, symbol, harus besar dan huruf kecil"
        );
    }


    @Deprecated
    private User giveValueToUserNew(RegisterDTO registerDTO) throws IOException {
        try{
            User user = new User();
            user.setName(registerDTO.getName());
            user.setNim(registerDTO.getNim());
            user.setPass(passwordEncoder.encode(registerDTO.getPass()));
            user.setRole(Role.valueOf(registerDTO.getRole()));
            user.setNameMateri(registerDTO.getNameMateri());
            user.setImage(registerDTO.getImage().getBytes());
            user.setCv(registerDTO.getCv().getBytes());
            return user;
        }catch (IOException exception){
            throw new IOException("kesalahan file inputan");
        }
    }

    @Deprecated
    @Override
    public ResponseAPI<?> registrasi(RegisterDTO registerDTO, Errors errors) throws IOException {
            try{
                if(
                        doubleNim(registerDTO.getNim()) &&
                                ErrorHandling.argumentErrorException(errors) &&
                                cekPassword(registerDTO.getPass()) &&
                                cekNameIsValid(registerDTO.getName())
                ){
                    User user = giveValueToUserNew(registerDTO);
                    userRepo.save(user);
                    var jwtToken = service.generatedToken(user);
                    return ResponseAPI.builder()
                            .code(HttpStatus.CREATED.value())
                            .token(jwtToken)
                            .message("berhasil menambahkan")
                            .build();
                }
            }catch (
                    DoubleNimException |
                            IllegalArgumentException |
                            InputMismatchException
                            exception
            ){

                List<String> error = new ArrayList<>();
                error.add(exception.getMessage());
                error.add(HttpStatus.INTERNAL_SERVER_ERROR.name());
                return ResponseAPI.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(error)
                        .message("gagal menambahkan")
                        .build();
            }
        return null;
    }

    public ResponseAPI<?> regis(){
        return null;
    }

    public AuthReponse login(LoginDTO loginDTO) {
        var user = loadUserByUsername(loginDTO.getNim());

        var jwtToken = service.generatedToken(user);
        return AuthReponse.builder()
                .token(jwtToken)
                .build();
    }


    @Override
    public User findByNim(String nim) {
        return userRepo.findByNim(nim).orElse(null);
    }


    private boolean cekNameIsValid(String name){
        boolean nameIsNumber = Pattern.compile("[a-zA-Z]").matcher(name).find();
        boolean nameIsSymbol = Pattern.compile("[\\p{Punct}]").matcher(name).find();
        if(!nameIsNumber || nameIsSymbol){
            throw new InputMismatchException("name is not valid");
        }
        return true;
    }

    @Override
    public ResponseAPI<?> updateNameUser(UpdateName name, Errors errors) {
        try {
            if (
                    ErrorHandling.argumentErrorException(errors) |
                            cekNameIsValid(name.getName())
            ) {
                userRepo.updateUser(name.getName(), service.extractUsername(name.getToken()));
                return ResponseAPI.builder()
                        .message("berhasil merubah nama")
                        .build();
            }
        }catch (InputMismatchException exception) {
            throw new InputMismatchException(exception.getMessage());
        }
        return null;
    }

    @Override
    public ResponseAPI<?> updateNameUser(UpdateName name, String nim, Errors errors){
        try{
            if(
                    ErrorHandling.argumentErrorException(errors)
            ){
                userRepo.updateUser(name.getName(), nim);
                return ResponseAPI.builder()
                        .code(HttpStatus.CREATED.value())
                        .message("berhasil merubah")
                        .build();
            }
            return null;
        }catch (
                InputMismatchException |
                IllegalArgumentException exception
        ){
            List<String> error = new ArrayList<>();
            error.add(exception.getMessage());
            error.add(HttpStatus.NOT_MODIFIED.name());

            return ResponseAPI.builder()
                    .code(HttpStatus.NOT_MODIFIED.value())
                    .message("gagal merubah")
                    .error(error)
                    .build();
        }
    }

            return ResponseAPI.builder()
                    .code(HttpStatus.NOT_MODIFIED.value())
                    .message("gagal merubah")
                    .error(errors)
                    .build();
        }
    }

}