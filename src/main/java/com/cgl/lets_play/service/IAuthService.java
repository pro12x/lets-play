package com.cgl.lets_play.service;

import com.cgl.lets_play.dto.AuthRequest;
import com.cgl.lets_play.dto.AuthResponse;
import com.cgl.lets_play.dto.UserDto;

public interface IAuthService {
    AuthResponse login(AuthRequest authRequest);
    AuthResponse register(UserDto userDto);
}
