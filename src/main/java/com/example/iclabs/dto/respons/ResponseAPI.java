package com.example.iclabs.dto.respons;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.List;

@Builder
public record ResponseAPI<T> (int code, String message, List<String> error, T data, String token) {
}
