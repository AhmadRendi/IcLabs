package com.example.iclabs.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.InputMismatchException;

@Component
public class ErrorHandling{

    public static boolean argumentErrorException(Errors errors){
        if(errors.hasErrors()){
            for(var error : errors.getAllErrors()){
                throw new InputMismatchException(error.getDefaultMessage());
            }
        }
        return true;
    }


    public static boolean notBlank(String field){
        if(field.isBlank()){
            throw new InputMismatchException("tidak boleh kosong");
        }
        return true;
    }
}
