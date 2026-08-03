package com.linkhub.service.impl;

import com.linkhub.dto.auth.AuthResponse;
import com.linkhub.dto.auth.LoginRequest;
import com.linkhub.dto.auth.RegisterRequest;
import com.linkhub.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}