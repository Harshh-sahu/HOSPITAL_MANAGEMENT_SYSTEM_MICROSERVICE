package com.hms.user.dto;

import com.hms.user.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOMappingTest {

    @Test
    void userDTO_toEntity_convertsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO dto = new UserDTO(
                5L, "John Doe", "john@email.com", "Pass@1234", 
                Roles.DOCTOR, 1001L, now, now
        );

        User entity = dto.toEntity();

        assertEquals(5L, entity.getId());
        assertEquals("John Doe", entity.getName());
        assertEquals("john@email.com", entity.getEmail());
        assertEquals("Pass@1234", entity.getPassword());
        assertEquals(Roles.DOCTOR, entity.getRole());
        assertEquals(1001L, entity.getProfileId());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    void user_toDTO_convertsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        User entity = new User(
                5L, "Jane Doe", "jane@email.com", "Pass@5678", 
                Roles.PATIENT, 2001L, now, now
        );

        UserDTO dto = entity.toDTO();

        assertEquals(5L, dto.getId());
        assertEquals("Jane Doe", dto.getName());
        assertEquals("jane@email.com", dto.getEmail());
        assertEquals("Pass@5678", dto.getPassword());
        assertEquals(Roles.PATIENT, dto.getRole());
        assertEquals(2001L, dto.getProfileId());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void userDTO_toEntity_withNullValues() {
        UserDTO dto = new UserDTO(
                null, "Test", "test@email.com", "Pass@1234", 
                Roles.DOCTOR, null, null, null
        );

        User entity = dto.toEntity();

        assertNull(entity.getId());
        assertEquals("Test", entity.getName());
        assertNull(entity.getProfileId());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    void roundTrip_dtoToEntityToDto() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO original = new UserDTO(
                10L, "Round Trip", "roundtrip@email.com", "Pass@9999", 
                Roles.PATIENT, 3001L, now, now
        );

        User entity = original.toEntity();
        UserDTO recovered = entity.toDTO();

        assertEquals(original.getId(), recovered.getId());
        assertEquals(original.getName(), recovered.getName());
        assertEquals(original.getEmail(), recovered.getEmail());
        assertEquals(original.getPassword(), recovered.getPassword());
        assertEquals(original.getRole(), recovered.getRole());
        assertEquals(original.getProfileId(), recovered.getProfileId());
        assertEquals(original.getCreatedAt(), recovered.getCreatedAt());
        assertEquals(original.getUpdatedAt(), recovered.getUpdatedAt());
    }

    @Test
    void userDTO_allFieldsNotNull() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO dto = new UserDTO(
                1L, "Complete", "complete@email.com", "Pass@0000", 
                Roles.DOCTOR, 5001L, now, now
        );

        assertNotNull(dto.getId());
        assertNotNull(dto.getName());
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getPassword());
        assertNotNull(dto.getRole());
        assertNotNull(dto.getProfileId());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    void userDTO_respectsAllRoles() {
        LocalDateTime now = LocalDateTime.now();
        
        UserDTO doctorDTO = new UserDTO(
                1L, "Doctor", "doc@email.com", "Pass@1234", 
                Roles.DOCTOR, 1001L, now, now
        );
        assertEquals(Roles.DOCTOR, doctorDTO.getRole());

        UserDTO patientDTO = new UserDTO(
                2L, "Patient", "pat@email.com", "Pass@1234", 
                Roles.PATIENT, 2001L, now, now
        );
        assertEquals(Roles.PATIENT, patientDTO.getRole());
    }
}

