package com.example.iclabs.service.impl;

import com.example.iclabs.config.CacheConfig;
import com.example.iclabs.dto.request.LoginDTO;
import com.example.iclabs.dto.request.RegisterDTO;
import com.example.iclabs.dto.respons.AuthReponse;
import com.example.iclabs.dto.respons.ResponseAPI;
import com.example.iclabs.entity.User;
import com.example.iclabs.repo.UserRepo;
import com.example.iclabs.security.jwt.JWTService;
import com.example.iclabs.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;

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
        return userRepo.findByEmail(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("email not found");
        });
    }


    @Override
    public AuthReponse registrasi(RegisterDTO registerDTO) {
        User user = modelMapper.map(registerDTO, User.class);
        user.setPass(passwordEncoder.encode(registerDTO.getPass()));
        userRepo.save(user);
        var jwtToken = service.generatedToken(user);
        return AuthReponse.builder()
                .token(jwtToken)
                .build();
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