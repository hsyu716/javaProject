package com.example.favoriteservice.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String userId;
    private String userName;
    private String email;
    private String account;
    
    public static LoginResponse success(String userId, String userName, String email, String account) {
        LoginResponse response = new LoginResponse();
        response.success = true;
        response.message = "登入成功";
        response.userId = userId;
        response.userName = userName;
        response.email = email;
        response.account = account;
        return response;
    }
    
    public static LoginResponse failure(String message) {
        LoginResponse response = new LoginResponse();
        response.success = false;
        response.message = message;
        return response;
    }
}
