package com.linkhub.service;

import com.linkhub.dto.auth.AuthResponse;
import com.linkhub.dto.auth.LoginRequest;
import com.linkhub.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}