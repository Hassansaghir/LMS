package com.LMS.Service;

import com.LMS.Dto.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service", url = "http://localhost:9091/api/emails")
public interface EmailServiceClient {

    @PostMapping
    String sendEmail(@RequestBody EmailRequest request);

}

