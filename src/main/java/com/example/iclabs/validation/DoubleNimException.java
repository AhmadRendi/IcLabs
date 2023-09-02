package com.example.iclabs.validation;

import org.springframework.stereotype.Component;

public class DoubleNimException extends RuntimeException{

    public DoubleNimException(String message){
         super(message);
    }
}
