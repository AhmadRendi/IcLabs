package com.example.iclabs.dto.request;


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
    private String email;
    private String pass;
    private String role = "USER";
}
