package com.example.iclabs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateName {

    @NotBlank(message = "nama tidak boleh kosong")
    private String name;

<<<<<<< HEAD
    private String token;
=======
    private String nim;
>>>>>>> 2fb10281cee9f45fa367f2f5f71ed1148e3e380f
}
