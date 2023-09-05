package com.example.iclabs.service.impl;

import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.RegisterDTO;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
@org.springframework.cache.annotation.CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService service;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByNim(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("email not found");
        });
    }

    private boolean doubleNim(String nim){
        if(getUserByNimCache(nim) != null){
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

    @Override
    public ResponseAPI<?> registrasi(RegisterDTO registerDTO, Errors errors) throws IOException {
            try{
                if(
                        doubleNim(registerDTO.getNim()) &&
                                ErrorHandling.argumentErrorException(errors) &&
                                cekPassword(registerDTO.getPass())
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

    public AuthReponse login(LoginDTO loginDTO) {
        var user = loadUserByUsername(loginDTO.getNim());

        /*
          * ini juga dapat digunakan untuk login
         */
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                loginDTO.getNim(), loginDTO.getPassword()
//        ));

        var jwtToken = service.generatedToken(user);
        return AuthReponse.builder()
                .token(jwtToken)
                .build();
    }

    @Cacheable(value = "userCache", key = "'userList'")
    public List<User> getAll() {
        return getAllFromDatabase();
    }

    private List<User> getAllFromDatabase() {
        return userRepo.findAll();
    }

    @Cacheable(key = "#id")
    public User getUserById(Long id) {
        return getById(id);
    }

    private User getUserByNim(String nim){
        Optional<User> optional = userRepo.findByNim(nim);
        return optional.orElse(null);
    }

    @Cacheable(key = "#nim")
    public User getUserByNimCache(String nim){
        return getUserByNim(nim);
    }

    public User getById(Long id) {
        Optional<User> optional = userRepo.findById(id);
        return optional.orElse(null);
    }

    @Override
    public void updateNameUser(String name, Long id) {
        userRepo.updateUser(name, id);
    }

    public void updateName(String name, Long id) {
        updateNameUser(name, id);
    }
}