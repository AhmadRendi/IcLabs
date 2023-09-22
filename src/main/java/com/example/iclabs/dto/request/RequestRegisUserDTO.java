package com.example.iclabs.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestRegisUserDTO {

    private String nim;

    private String name;

    private String pass;

}
