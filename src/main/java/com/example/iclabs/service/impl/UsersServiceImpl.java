package com.example.iclabs.service.impl;

import com.example.iclabs.dto.request.RequestRegisUserDTO;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.CalonAsisten;
import com.example.iclabs.repo.CalonAsistenRepo;
import com.example.iclabs.role.Role;
import com.example.iclabs.security.jwt.JWTService;
import com.example.iclabs.service.UsersService;
import com.example.iclabs.validation.DoubleNimException;
import com.example.iclabs.validation.ErrorHandling;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class UsersServiceImpl  implements UserDetailsService, UsersService{


    private ObjectMapper objectMapper;

    private CalonAsistenRepo usersRepo;

    private JWTService service;

    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepo.findByNim(username).orElseThrow(() -> new UsernameNotFoundException("nim tidak ditemukan"));
    }


    private CalonAsisten confertRequestRegisUserDTOToUsers(RequestRegisUserDTO userDTO){
        CalonAsisten calonAsisten = new CalonAsisten();
        calonAsisten.setName(userDTO.getName());
        calonAsisten.setNim(userDTO.getNim());
        String pass = passwordEncoder.encode(userDTO.getPass());
        calonAsisten.setPassword(pass);
        calonAsisten.setRole(Role.valueOf("USER"));

        return calonAsisten;
    }

    private boolean cekDoubleNim(String nim){
        if(usersRepo.findByNim(nim).isPresent()){
            throw new DoubleNimException("nim telah tersedia");
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

    public boolean cekNameIsValid(String name){
        boolean nameIsNumber = Pattern.compile("[a-zA-Z]").matcher(name).find();
        boolean nameIsSymbol = Pattern.compile("[\\p{Punct}]").matcher(name).find();
        if(!nameIsNumber || nameIsSymbol){
            throw new InputMismatchException("name is not valid");
        }
        return true;
    }

    @Override
    public ResponseAPI<?> registration(RequestRegisUserDTO userDTO, Errors errors) {

        try{
            if(
                   cekNameIsValid(userDTO.getName()) &&
                           cekPassword(userDTO.getPass()) &&
                           cekDoubleNim(userDTO.getNim()) &&
                           ErrorHandling.argumentErrorException(errors)
            ){
                log.info("ini masih class DTO : " + userDTO.getName());
                CalonAsisten users = confertRequestRegisUserDTOToUsers(userDTO);
                log.info(users.getName());
                usersRepo.save(users);
                return ResponseAPI.builder()
                        .code(HttpStatus.CREATED.value())
                        .data(users)
                        .message("Berhasil Menambahkan")
                        .build();
            }
        }catch (
                InputMismatchException |
                        IllegalArgumentException |
                        DoubleNimException exception
        ){
            List<String> error = new ArrayList<>();
            error.add(exception.getMessage());
            error.add(HttpStatus.INTERNAL_SERVER_ERROR.name());

            return ResponseAPI.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Gagal Menambahkan")
                    .error(error)
                    .build();
        }
        return null;
    }
}
