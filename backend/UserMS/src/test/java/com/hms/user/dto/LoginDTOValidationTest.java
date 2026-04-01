package com.hms.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginDTOValidationTest {

    @Test
    void loginDTO_creation_success() {
        LoginDTO login = new LoginDTO("user@email.com", "Pass@1234");

        assertEquals("user@email.com", login.getEmail());
        assertEquals("Pass@1234", login.getPassword());
    }

    @Test
    void loginDTO_setters_work() {
        LoginDTO login = new LoginDTO();
        login.setEmail("test@email.com");
        login.setPassword("SecurePass@5678");

        assertEquals("test@email.com", login.getEmail());
        assertEquals("SecurePass@5678", login.getPassword());
    }

    @Test
    void loginDTO_nullEmail_allowedByConstructor() {
        LoginDTO login = new LoginDTO(null, "Pass@1234");

        assertNull(login.getEmail());
        assertEquals("Pass@1234", login.getPassword());
    }

    @Test
    void loginDTO_nullPassword_allowedByConstructor() {
        LoginDTO login = new LoginDTO("user@email.com", null);

        assertEquals("user@email.com", login.getEmail());
        assertNull(login.getPassword());
    }

    @Test
    void loginDTO_noArgsConstructor_createsEmptyObject() {
        LoginDTO login = new LoginDTO();

        assertNull(login.getEmail());
        assertNull(login.getPassword());
    }

    @Test
    void loginDTO_equality() {
        LoginDTO login1 = new LoginDTO("user@email.com", "Pass@1234");
        LoginDTO login2 = new LoginDTO("user@email.com", "Pass@1234");

        assertEquals(login1, login2);
    }

    @Test
    void loginDTO_inequality_differentEmail() {
        LoginDTO login1 = new LoginDTO("user1@email.com", "Pass@1234");
        LoginDTO login2 = new LoginDTO("user2@email.com", "Pass@1234");

        assertNotEquals(login1, login2);
    }

    @Test
    void loginDTO_inequality_differentPassword() {
        LoginDTO login1 = new LoginDTO("user@email.com", "Pass@1234");
        LoginDTO login2 = new LoginDTO("user@email.com", "Pass@5678");

        assertNotEquals(login1, login2);
    }

    @Test
    void loginDTO_toString_includessFields() {
        LoginDTO login = new LoginDTO("user@email.com", "Pass@1234");
        String str = login.toString();

        assertTrue(str.contains("user@email.com") || str.contains("email"));
    }
}

