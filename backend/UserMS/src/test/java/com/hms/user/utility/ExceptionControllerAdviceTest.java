package com.hms.user.utility;

import com.hms.user.exception.HmsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionControllerAdviceTest {

    @Test
    void exceptionHandler_genericException_returns500() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        Exception e = new RuntimeException("Something went wrong");

        var response = advice.exceptionHandler(e);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Some error Occured", response.getBody().getErrorMessage());
        assertEquals(500, response.getBody().getErrorCode());
        assertNotNull(response.getBody().getTimeStamp());
    }

    @Test
    void hmsExceptionHandler_withValidMessage_returnsPropertyValue() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        Environment env = new MockEnvironment()
                .withProperty("USER_NOT_FOUND", "User not found in system");
        ReflectionTestUtils.setField(advice, "environment", env);

        HmsException e = new HmsException("USER_NOT_FOUND");
        var response = advice.HmsExceptionHandler(e);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found in system", response.getBody().getErrorMessage());
        assertEquals(500, response.getBody().getErrorCode());
    }

    @Test
    void hmsExceptionHandler_withNullProperty_returnsNull() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        Environment env = new MockEnvironment();
        ReflectionTestUtils.setField(advice, "environment", env);

        HmsException e = new HmsException("NONEXISTENT_KEY");
        var response = advice.HmsExceptionHandler(e);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getErrorMessage());
    }

    @Test
    void handleValidationException_constraintViolationException_returns400() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        
        // Create a mock ConstraintViolation
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException e = new ConstraintViolationException("Validation error", violations);

        var response = advice.handleValidationException(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getErrorCode());
        assertNotNull(response.getBody().getTimeStamp());
    }

    @Test
    void handleValidationException_methodArgumentNotValidException_returns400() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        
        // Mock a MethodArgumentNotValidException
        org.springframework.validation.BeanPropertyBindingResult bindingResult = 
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new org.springframework.validation.FieldError(
                "object", "email", "must be a valid email"
        ));
        
        MethodArgumentNotValidException e = new MethodArgumentNotValidException(
                null, bindingResult
        );

        var response = advice.handleValidationException(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getErrorCode());
        assertTrue(response.getBody().getErrorMessage().contains("must be a valid email"));
    }

    @Test
    void errorInfo_creationAndRetrieval() {
        LocalDateTime now = LocalDateTime.now();
        ErrorInfo error = new ErrorInfo("Test error", 400, now);

        assertEquals("Test error", error.getErrorMessage());
        assertEquals(400, error.getErrorCode());
        assertEquals(now, error.getTimeStamp());
    }
}

