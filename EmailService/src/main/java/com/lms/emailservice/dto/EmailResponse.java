package com.lms.emailservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailResponse(
        String status,
        @NotBlank @Email String email
) {}
