package com.example.iclabs.dto.request;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String name;
    @Email(message = "email tidak valid")
    private String nim;
    private String pass;

    private MultipartFile image;

    private String nameMateri;

    private MultipartFile cv;

    private String role = "USER";
}
