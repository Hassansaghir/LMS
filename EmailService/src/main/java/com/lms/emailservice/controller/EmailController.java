package com.lms.emailservice.controller;

import com.lms.emailservice.dto.EmailRequest;
import com.lms.emailservice.dto.EmailResponse;
import com.lms.emailservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    @Operation(summary = "Sending email text")
    @ApiResponse(responseCode = "202",description = "Email sent")
    @PostMapping
    public ResponseEntity<EmailResponse> sendEmail(@Valid @RequestBody EmailRequest request) {
        emailService.sendEmail(request.email(), request.message());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new EmailResponse("Queued",request.email()));
    }
}
