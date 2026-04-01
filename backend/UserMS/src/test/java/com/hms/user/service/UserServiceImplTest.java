package com.hms.user.service;

import com.hms.user.clients.ProfileClient;
import com.hms.user.dto.Roles;
import com.hms.user.dto.UserDTO;
import com.hms.user.entity.User;
import com.hms.user.exception.HmsException;
import com.hms.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileClient profileClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO sampleUserDTO(Long id, String email, Roles role) {
        return new UserDTO(
                id, "Test User", email, "Pass@1234", role, 1001L, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private User sampleUser(Long id, String email, Roles role) {
        return new User(
                id, "Test User", email, "encodedPass", role, 1001L, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void registerUser_userAlreadyExists_throws() {
        UserDTO dto = sampleUserDTO(null, "test@email.com", Roles.DOCTOR);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(sampleUser(1L, "test@email.com", Roles.DOCTOR)));

        HmsException exception = assertThrows(HmsException.class, () -> userService.registerUser(dto));
        assertEquals("USER_ALREADY_EXISTS", exception.getMessage());
    }

    @Test
    void registerUser_doctorRole_callsProfileClient() throws HmsException {
        UserDTO dto = sampleUserDTO(null, "doc@email.com", Roles.DOCTOR);
        when(userRepository.findByEmail("doc@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Pass@1234")).thenReturn("encodedPass");
        when(profileClient.addDoctor(any(UserDTO.class))).thenReturn(2001L);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser(10L, "doc@email.com", Roles.DOCTOR));

        userService.registerUser(dto);

        verify(profileClient, times(1)).addDoctor(any(UserDTO.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_patientRole_callsProfileClient() throws HmsException {
        UserDTO dto = sampleUserDTO(null, "pat@email.com", Roles.PATIENT);
        when(userRepository.findByEmail("pat@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Pass@1234")).thenReturn("encodedPass");
        when(profileClient.addPatient(any(UserDTO.class))).thenReturn(3001L);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser(11L, "pat@email.com", Roles.PATIENT));

        userService.registerUser(dto);

        verify(profileClient, times(1)).addPatient(any(UserDTO.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_userNotFound_throws() {
        UserDTO dto = sampleUserDTO(null, "notfound@email.com", Roles.DOCTOR);
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        HmsException exception = assertThrows(HmsException.class, () -> userService.login(dto));
        assertEquals("user Not found", exception.getMessage());
    }

    @Test
    void login_invalidPassword_throws() {
        UserDTO dto = sampleUserDTO(null, "test@email.com", Roles.DOCTOR);
        User user = sampleUser(1L, "test@email.com", Roles.DOCTOR);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@1234", "encodedPass")).thenReturn(false);

        HmsException exception = assertThrows(HmsException.class, () -> userService.login(dto));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());
    }

    @Test
    void login_validCredentials_returnsUserDtoWithoutPassword() throws HmsException {
        UserDTO dto = sampleUserDTO(null, "test@email.com", Roles.DOCTOR);
        User user = sampleUser(1L, "test@email.com", Roles.DOCTOR);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@1234", "encodedPass")).thenReturn(true);

        UserDTO result = userService.login(dto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertNull(result.getPassword());
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        HmsException exception = assertThrows(HmsException.class, () -> userService.getUserById(99L));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }

    @Test
    void getUserById_found_returnsUserDTO() throws HmsException {
        User user = sampleUser(1L, "test@email.com", Roles.DOCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void getUser_notFound_throws() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        HmsException exception = assertThrows(HmsException.class, () -> userService.getUser("notfound@email.com"));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }

    @Test
    void getUser_found_returnsUserDTO() throws HmsException {
        User user = sampleUser(1L, "test@email.com", Roles.DOCTOR);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUser("test@email.com");

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void getProfile_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        HmsException exception = assertThrows(HmsException.class, () -> userService.getProfile(99L));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }

    @Test
    void getProfile_doctorRole_callsProfileClient() throws HmsException {
        User user = sampleUser(1L, "doc@email.com", Roles.DOCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileClient.getDoctor(1001L)).thenReturn(1001L);

        Long result = userService.getProfile(1L);

        assertEquals(1001L, result);
        verify(profileClient, times(1)).getDoctor(1001L);
    }

    @Test
    void getProfile_patientRole_callsProfileClient() throws HmsException {
        User user = sampleUser(1L, "pat@email.com", Roles.PATIENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileClient.getPatient(1001L)).thenReturn(1001L);

        Long result = userService.getProfile(1L);

        assertEquals(1001L, result);
        verify(profileClient, times(1)).getPatient(1001L);
    }

    @Test
    void getProfile_invalidRole_throws() throws HmsException {
        User user = new User(1L, "Test", "test@email.com", "pass", null, 1001L, LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When role is null, the service will throw NullPointerException, not HmsException
        assertThrows(NullPointerException.class, () -> userService.getProfile(1L));
    }
}

