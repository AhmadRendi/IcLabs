package com.example.iclabs.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "nama tidak boleh kosong")
    private String name;

    @NotBlank(message = "nim tidak boleh kosong")
    private String nim;

    @NotBlank(message = "password tidak boleh kosong")
    private String pass;

    private MultipartFile image;

    @NotBlank(message = "materi tidak boleh kosong")
    private String nameMateri;

    private MultipartFile cv;

    private String role = "USER";
}
