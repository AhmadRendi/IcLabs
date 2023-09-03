package com.example.iclabs.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class ErrorHandling{

    public static boolean argumentErrorException(Errors errors){
        if(errors.hasErrors()){
            for(var error : errors.getAllErrors()){
                throw new IllegalArgumentException(error.getDefaultMessage());
            }
        }
        return true;
    }
}
