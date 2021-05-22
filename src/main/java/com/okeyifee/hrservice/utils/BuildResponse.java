package com.okeyifee.hrservice.utils;


import com.okeyifee.hrservice.payload.ApiResponse;
import org.springframework.http.ResponseEntity;

public class BuildResponse {

    public static <T> ResponseEntity<ApiResponse> buildResponse(ApiResponse<T> response) {
        return new ResponseEntity<>(response, response.getStatus());
    }
}
