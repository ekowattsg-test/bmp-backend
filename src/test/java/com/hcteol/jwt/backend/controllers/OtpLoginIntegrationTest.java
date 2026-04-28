package com.hcteol.jwt.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.services.MobileLoginService;
import com.hcteol.jwt.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MobileLoginController.class)
@AutoConfigureMockMvc
public class OtpLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private MobileLoginService mobileLoginService;
        @MockBean
        private com.hcteol.jwt.backend.config.UserAuthenticationProvider userAuthenticationProvider;
    @MockBean
    private com.hcteol.jwt.backend.repositories.StaffRepository staffRepository;
    @MockBean
    private com.hcteol.jwt.backend.services.UserLoginService userLoginService;

    @Test
    public void otpLogin_success_marksUsed() throws Exception {
        UserDto dto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .login("jdoe")
                .mobileNumber("5550001111")
                .build();

        when(userService.otpLogin("123456")).thenReturn(dto);
        when(userAuthenticationProvider.createToken(dto)).thenReturn("jwt-token-xyz");

        dto.setToken("jwt-token-xyz");

        var payload = objectMapper.writeValueAsString(java.util.Collections.singletonMap("otp", "123456"));

        mockMvc.perform(post("/api/mobile-logins/verify").with(user("testuser").roles("USER")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

                verify(userService, times(1)).otpLogin("123456");
                verify(userAuthenticationProvider, times(1)).createToken(org.mockito.ArgumentMatchers.any());
    }
}
