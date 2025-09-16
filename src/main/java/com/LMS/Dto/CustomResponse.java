package com.LMS.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T> {
    private String status;   // "success" or "error"
    private String message;  // optional message
    private T data;          // generic data
}
