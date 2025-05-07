package com.cgl.lets_play.service.impl;


import com.cgl.lets_play.dto.UserDto;
import com.cgl.lets_play.exception.ResourceNotFoundException;
import com.cgl.lets_play.exception.UserAlreadyExistsException;
import com.cgl.lets_play.model.User;
import com.cgl.lets_play.repository.UserRepository;
import com.cgl.lets_play.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDto(user);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole() != null && !userDto.getRole().isEmpty() ? userDto.getRole() : "ROLE_USER")
                .build();

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getPassword() == null) {
            existingUser.setPassword(existingUser.getPassword());
        }

        if (userDto.getRole() != null) {
            existingUser.setRole(userDto.getRole());
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
