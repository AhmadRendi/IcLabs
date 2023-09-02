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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@org.springframework.cache.annotation.CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
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
        if(getUserByNim(nim) != null){
            throw new DoubleNimException("nim telah digunakan");
        }
        return true;
    }


    @Override
    public ResponseAPI<?> registrasi(RegisterDTO registerDTO) throws IOException {
        try{
            if(doubleNim(registerDTO.getNim())){

                User user = new User();
                user.setName(registerDTO.getName());
                user.setNim(registerDTO.getNim());
                user.setPass(passwordEncoder.encode(registerDTO.getPass()));
                user.setRole(Role.valueOf(registerDTO.getRole()));
                user.setNameMateri(registerDTO.getNameMateri());
                user.setImage(registerDTO.getImage().getBytes());
                user.setCv(registerDTO.getCv().getBytes());

                userRepo.save(user);
                var jwtToken = service.generatedToken(user);
                return ResponseAPI.builder()
                        .code(HttpStatus.CREATED.value())
                        .token(jwtToken)
                        .message("berhasil menambahkan")
                        .build();
            }
        }catch (DoubleNimException exception){
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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getNim(), loginDTO.getPassword()
        ));
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

    @Cacheable(key = "#nim")
    public User getUserByNim(String nim){
        if(userRepo.findByNim(nim).isPresent()){
            return userRepo.findByNim(nim).get();
        }
        return null;
    }

    public User getById(Long id) {
        if (userRepo.findById(id).isPresent()) {
            return userRepo.findById(id).get();
        }
        return null;
    }

    @Override
    public void updateNameUser(String name, Long id) {
        userRepo.updateUser(name, id);
    }

    public void updateName(String name, Long id) {
        updateNameUser(name, id);
    }
}