package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.dtos.ChangePasswordDto;
import com.hcteol.jwt.backend.dtos.CredentialsDto;
import com.hcteol.jwt.backend.dtos.SignUpDto;
import com.hcteol.jwt.backend.dtos.UserDto;
// import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.mappers.UserMapper;
import com.hcteol.jwt.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hcteol.jwt.backend.exceptions.AppException;

import java.nio.CharBuffer;
import java.util.Date;
// import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        var user = userRepository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (user.getActive() == null || user.getActive() == 0) {
            throw new AppException("User account is inactive", HttpStatus.FORBIDDEN);
        }

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        var optionalUser = userRepository.findByLogin(userDto.getLogin());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        var user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
    user.setCompanyId(userDto.getCompanyId());
    user.setLevel(userDto.getLevel());
        // ensure new users are active by default
        if (user.getActive() == null) {
            user.setActive(1);
        }

        var savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByLogin(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public java.util.List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setLogin(userDto.getLogin());
        user.setCompanyId(userDto.getCompanyId());
        user.setLevel(userDto.getLevel());
        // only update active when client explicitly provides it
        if (userDto.getActive() != null) {
            user.setActive(userDto.getActive());
        }
        // Optionally update other fields
        var updatedUser = userRepository.save(user);
        return userMapper.toUserDto(updatedUser);
    }

    public void changePassword(Long id, ChangePasswordDto changePasswordDto, PasswordEncoder passwordEncoder) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        
        // Validate the old password
        if (!passwordEncoder.matches(CharBuffer.wrap(changePasswordDto.getOldPassword()), user.getPassword())) {
            throw new AppException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }
        
        // Set the new password and update lastPasswordChanged
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(changePasswordDto.getNewPassword())));
        user.setLastPasswordChanged(new Date());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

}
