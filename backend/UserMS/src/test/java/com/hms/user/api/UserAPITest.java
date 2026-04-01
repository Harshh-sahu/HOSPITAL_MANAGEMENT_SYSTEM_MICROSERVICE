package com.hms.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.user.dto.*;
import com.hms.user.exception.HmsException;
import com.hms.user.jwt.JwtUtil;
import com.hms.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAPI.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    private UserDTO sampleUserDTO(Long id, String email, Roles role) {
        return new UserDTO(
                id, "Test User", email, "Pass@1234", role, 1001L, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void registerUser_validInput_returns201() throws Exception {
        UserDTO dto = sampleUserDTO(null, "newuser@email.com", Roles.DOCTOR);
        doNothing().when(userService).registerUser(any(UserDTO.class));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Account created."));
    }

    @Test
    void registerUser_userAlreadyExists_returns500() throws Exception {
        UserDTO dto = sampleUserDTO(null, "existing@email.com", Roles.DOCTOR);
        doThrow(new HmsException("USER_ALREADY_EXISTS")).when(userService).registerUser(any(UserDTO.class));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerUser_invalidEmail_returns400() throws Exception {
        UserDTO dto = new UserDTO(
                null, "Test User", "invalid-email", "Pass@1234", Roles.DOCTOR, null, LocalDateTime.now(), LocalDateTime.now()
        );

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_weakPassword_returns400() throws Exception {
        UserDTO dto = new UserDTO(
                null, "Test User", "test@email.com", "weak", Roles.DOCTOR, null, LocalDateTime.now(), LocalDateTime.now()
        );

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_validCredentials_returns200WithToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@email.com", "Pass@1234");
        UserDetails userDetails = new User("test@email.com", "encodedPass", new ArrayList<>());

        when(userDetailsService.loadUserByUsername("test@email.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt.token.here");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt.token.here"));
    }

    @Test
    void loginUser_invalidCredentials_returns500() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@email.com", "WrongPassword");

        when(userDetailsService.loadUserByUsername("test@email.com"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/user/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test"));
    }

    @Test
    void getProfile_validUserId_returns200() throws Exception {
        when(userService.getProfile(1L)).thenReturn(2001L);

        mockMvc.perform(get("/user/getProfile/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("2001"));
    }

    @Test
    void getProfile_userNotFound_returns500() throws Exception {
        when(userService.getProfile(99L)).thenThrow(new HmsException("USER_NOT_FOUND"));

        mockMvc.perform(get("/user/getProfile/99"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMonthlyRegistrationCounts_returns200() throws Exception {
        List<MonthlyRoleCountDTO> doctorCounts = List.of(
                new MonthlyRoleCountDTO("2026-01", 5L),
                new MonthlyRoleCountDTO("2026-02", 3L)
        );
        List<MonthlyRoleCountDTO> patientCounts = List.of(
                new MonthlyRoleCountDTO("2026-01", 10L),
                new MonthlyRoleCountDTO("2026-02", 8L)
        );
        RegistrationCountDTO counts = new RegistrationCountDTO(doctorCounts, patientCounts);

        when(userService.getMonthlyRegistrationCounts()).thenReturn(counts);

        mockMvc.perform(get("/user/getRegistrationCounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorCounts[0].month").value("2026-01"))
                .andExpect(jsonPath("$.doctorCounts[0].count").value(5))
                .andExpect(jsonPath("$.patientCounts[0].month").value("2026-01"))
                .andExpect(jsonPath("$.patientCounts[0].count").value(10));
    }

    @Test
    void getMonthlyRegistrationCounts_error_returns500() throws Exception {
        when(userService.getMonthlyRegistrationCounts()).thenThrow(new HmsException("DATABASE_ERROR"));

        mockMvc.perform(get("/user/getRegistrationCounts"))
                .andExpect(status().isInternalServerError());
    }
}

