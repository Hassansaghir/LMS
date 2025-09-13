package com.LMS.library_management.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BorrowerDTO {

    @NotNull
    private UUID id;

    private String name;

    @Email(message = "Email should be valid")

    private String email;

    private String cardNumber;

    private String phoneNumber;

}
