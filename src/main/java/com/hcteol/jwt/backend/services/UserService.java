package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.dtos.ChangePasswordDto;
import com.hcteol.jwt.backend.dtos.CredentialsDto;
import com.hcteol.jwt.backend.dtos.SignUpDto;
import com.hcteol.jwt.backend.dtos.UserDto;
// import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.mappers.UserMapper;
import com.hcteol.jwt.backend.repositories.UserRepository;
import com.hcteol.jwt.backend.entities.UserLogin;
import com.hcteol.jwt.backend.services.UserLoginService;
import com.hcteol.jwt.backend.repositories.MobileLoginRepository;
import com.hcteol.jwt.backend.entities.MobileLogin;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.time.ZonedDateTime;
import java.time.ZoneId;
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
    private final UserLoginService userLoginService;
    private final MobileLoginRepository mobileLoginRepository;
    @Value("${OTP_QR_LOGIN_TIMEOUT_MINUTES:2}")
    private long otpQrLoginTimeoutMinutes;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserDto login(CredentialsDto credentialsDto) {
        var user = userRepository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (user.getActive() == null || user.getActive() == 0) {
            throw new AppException("User account is inactive", HttpStatus.FORBIDDEN);
        }

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            // record successful login
            UserLogin userLogin = UserLogin.builder()
                    .userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .loginType("PASSWORD")
                    .timeLogin(java.time.LocalDateTime.now())
                    .build();
            userLoginService.addUserLogin(userLogin);
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    /**
     * Authenticate credentials without recording a UserLogin entry.
     */
    public UserDto authenticate(CredentialsDto credentialsDto) {
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

    public UserDto otpLogin(String otp) {
        var mobileLogin = mobileLoginRepository.findByOtp(otp)
                .orElseThrow(() -> new AppException("Invalid OTP", HttpStatus.UNAUTHORIZED));

        if (mobileLogin.getStatus() == null || !"NEW".equals(mobileLogin.getStatus())) {
            throw new AppException("OTP already used or invalid", HttpStatus.UNAUTHORIZED);
        }

        // Use injected property and compare times in a timezone-aware way
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime requestZdt = null;
        if (mobileLogin.getRequestTime() != null) {
            requestZdt = mobileLogin.getRequestTime().atZone(ZoneId.systemDefault());
        }
        ZonedDateTime cutoff = now.minusMinutes(otpQrLoginTimeoutMinutes);
        logger.debug("OTP verify: otp={}, requestTime={}, now={}, cutoff={}, timeoutMinutes={}",
                otp, requestZdt, now, cutoff, otpQrLoginTimeoutMinutes);

        if (requestZdt == null || requestZdt.isBefore(cutoff)) {
            throw new AppException("OTP expired", HttpStatus.UNAUTHORIZED);
        }

        var user = userRepository.findByMobileNumber(mobileLogin.getMobileNumber())
                .orElseThrow(() -> new AppException("User not found for mobile number", HttpStatus.NOT_FOUND));

        UserLogin userLogin = UserLogin.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .loginType("OTP")
                .timeLogin(java.time.LocalDateTime.now())
                .build();

        userLoginService.addUserLogin(userLogin);

        // mark mobile login as used
        mobileLogin.setStatus("USED");
        mobileLoginRepository.save(mobileLogin);

        return userMapper.toUserDto(user);
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
        user.setMobileNumber(userDto.getMobileNumber());
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
