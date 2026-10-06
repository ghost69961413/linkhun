package com.linkhub.service.impl;

import com.linkhub.dto.auth.AuthResponse;
import com.linkhub.dto.auth.LoginRequest;
import com.linkhub.dto.auth.RegisterRequest;
import com.linkhub.entity.Profile;
import com.linkhub.entity.User;
import com.linkhub.enums.Role;
import com.linkhub.enums.ProfileType;
import com.linkhub.exception.ResourceAlreadyExistsException;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.security.CustomUserDetails;
import com.linkhub.security.JwtService;
import com.linkhub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByUsernameIgnoreCase(request.getUsername().trim())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .accountVerified(true) // Development ke liye true
                .build();

        User savedUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(savedUser)
                .profileType(request.getProfileType() == null ? ProfileType.PROFESSIONAL : request.getProfileType())
                .followers(0)
                .following(0)
                .build();

        profileRepository.save(profile);

        String token = jwtService.generateToken(new CustomUserDetails(savedUser));

        return AuthResponse.builder()
                .token(token)
                .message("Registration Successful")
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        String identifier = request.getUsernameOrEmail().trim();
        User user = identifier.contains("@")
                ? userRepository.findByEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Invalid username/email or password"))
                : userRepository.findByUsernameIgnoreCase(identifier)
                    .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Invalid username/email or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .message("Login Successful")
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
