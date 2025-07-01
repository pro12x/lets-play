package com.cgl.lets_play.service.impl;

import com.cgl.lets_play.dto.AuthRequest;
import com.cgl.lets_play.dto.AuthResponse;
import com.cgl.lets_play.dto.UserDto;
import com.cgl.lets_play.exception.ResourceNotFoundException;
import com.cgl.lets_play.model.User;
import com.cgl.lets_play.repository.UserRepository;
import com.cgl.lets_play.security.JwtTokenUtil;
import com.cgl.lets_play.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public AuthResponse register(UserDto userDto) {
        UserDto createUser = userService.addUser(userDto);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(createUser.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(createUser.getEmail())
                .role(createUser.getRole())
                .build();
    }
}
