package com.pft.dto.auth;

import com.pft.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private UserDto user;
    
    public AuthResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }
} 