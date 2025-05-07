package com.cgl.lets_play.service;

import com.cgl.lets_play.dto.UserDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(String id);
    UserDto getCurrentUser();
    UserDto addUser(UserDto userDto);
    UserDto updateUser(String id, UserDto userDto);
    void deleteUser(String id);
}
