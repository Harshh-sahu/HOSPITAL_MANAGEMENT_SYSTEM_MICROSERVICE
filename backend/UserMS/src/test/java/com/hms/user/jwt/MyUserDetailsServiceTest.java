
package com.hms.user.jwt;

import com.hms.user.dto.Roles;
import com.hms.user.dto.UserDTO;
import com.hms.user.exception.HmsException;
import com.hms.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private MyUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_userFound_returnsCustomUserDetails() throws Exception {
        UserDTO userDTO = new UserDTO(
                1L, "Test User", "test@email.com", "encodedPassword", 
                Roles.DOCTOR, 1001L, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userService.getUser("test@email.com")).thenReturn(userDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@email.com");

        assertNotNull(userDetails);
        assertEquals("test@email.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_userNotFound_returnsNull() throws Exception {
        when(userService.getUser("notfound@email.com")).thenThrow(new HmsException("USER_NOT_FOUND"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("notfound@email.com");

        assertNull(userDetails);
    }

    @Test
    void loadUserByUsername_exceptionHandling_graceful() {
        when(userService.getUser("error@email.com")).thenThrow(new HmsException("DATABASE_ERROR"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("error@email.com");

        assertNull(userDetails);
    }

    @Test
    void loadUserByUsername_doctorRole_preservesRole() throws Exception {
        UserDTO userDTO = new UserDTO(
                1L, "Dr. Smith", "doctor@email.com", "pass123", 
                Roles.DOCTOR, 1001L, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userService.getUser("doctor@email.com")).thenReturn(userDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername("doctor@email.com");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        CustomUserDetails customDetails = (CustomUserDetails) userDetails;
        assertEquals(Roles.DOCTOR, customDetails.getRole());
    }

    @Test
    void loadUserByUsername_patientRole_preservesRole() throws Exception {
        UserDTO userDTO = new UserDTO(
                2L, "Patient John", "patient@email.com", "pass456", 
                Roles.PATIENT, 2001L, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userService.getUser("patient@email.com")).thenReturn(userDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername("patient@email.com");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        CustomUserDetails customDetails = (CustomUserDetails) userDetails;
        assertEquals(Roles.PATIENT, customDetails.getRole());
    }

    @Test
    void customUserDetails_creation_success() {
        CustomUserDetails details = new CustomUserDetails(
                1L, "test@email.com", "pass123", Roles.DOCTOR, 
                "Dr. Test", "test@email.com", 1001L, null
        );

        assertEquals(1L, details.getId());
        assertEquals("test@email.com", details.getUsername());
        assertEquals("pass123", details.getPassword());
        assertEquals(Roles.DOCTOR, details.getRole());
        assertEquals("Dr. Test", details.getName());
        assertEquals("test@email.com", details.getEmail());
        assertEquals(1001L, details.getProfileId());
    }

    @Test
    void customUserDetails_implementsUserDetails() {
        CustomUserDetails details = new CustomUserDetails(
                1L, "test@email.com", "pass123", Roles.DOCTOR, 
                "Dr. Test", "test@email.com", 1001L, null
        );

        assertTrue(details instanceof org.springframework.security.core.userdetails.UserDetails);
    }
}

