package com.example.iclabs.dto.request;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String name;
    @Email(message = "email tidak valid")
    private String email;
    private String pass;
    private String role = "USER";
}
