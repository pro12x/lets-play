package com.cgl.lets_play.service.impl;

import com.cgl.lets_play.dto.ChangeRoleRequest;
import com.cgl.lets_play.dto.UpdateRequest;
import com.cgl.lets_play.dto.UserDto;
import com.cgl.lets_play.exception.ResourceNotFoundException;
import com.cgl.lets_play.exception.UnauthorizedException;
import com.cgl.lets_play.exception.UserAlreadyExistsException;
import com.cgl.lets_play.model.User;
import com.cgl.lets_play.repository.UserRepository;
import com.cgl.lets_play.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return convertToDto(user);
    }

    @Override
    public UserDto getCurrentUser() {
        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
    public UserDto updateUserInfo(String id, UpdateRequest userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        boolean isEmailChanged = userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail());
        if (isEmailChanged && userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        existingUser.setName(userDto.getName() == null ? existingUser.getName() : userDto.getName());
        existingUser.setEmail(userDto.getEmail() == null ? existingUser.getEmail() : userDto.getEmail());

        /*if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getPassword() == null) {
            existingUser.setPassword(existingUser.getPassword());
        }*/

        /*if (userDto.getRole() != null) {
            existingUser.setRole(userDto.getRole());
        }*/

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

    public void changePassword(String id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean isOwner(String userId) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getId().equals(userId);
    }

    public void changeUserRole(String id, ChangeRoleRequest roleRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (roleRequest.getRole() == null || roleRequest.getRole().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        if (!roleRequest.getRole().equals("USER") && !roleRequest.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("Role must be either USER or ADMIN");
        }

        user.setRole("ROLE_" + roleRequest);
        userRepository.save(user);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            throw new UnauthorizedException("User is not authenticated");
        }
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
