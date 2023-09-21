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

    private String token;
}
