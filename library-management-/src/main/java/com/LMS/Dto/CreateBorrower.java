package com.LMS.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBorrower {

    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private String cardNumber;

    private String phoneNumber;
}
