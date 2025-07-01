package com.cgl.lets_play.service;

import com.cgl.lets_play.dto.ChangeRoleRequest;
import com.cgl.lets_play.dto.UpdateRequest;
import com.cgl.lets_play.dto.UserDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(String id);
    UserDto getCurrentUser();
    UserDto addUser(UserDto userDto);
    UserDto updateUserInfo(String id, UpdateRequest userDto);
    void deleteUser(String id);
    void changePassword(String id, String oldPassword, String newPassword);
    void changeUserRole(String id, ChangeRoleRequest roleRequest);
    boolean isOwner(String userId);
}
