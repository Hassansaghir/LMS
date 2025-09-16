package com.LMS.Dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BorrowerDTO {

    private UUID id;

    private String name;


    private String email;

    private String cardNumber;

    private String phoneNumber;

}
