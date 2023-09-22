package com.example.iclabs.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestRegisUser {

    private String nim;

    private String name;

    private String pass;

}
