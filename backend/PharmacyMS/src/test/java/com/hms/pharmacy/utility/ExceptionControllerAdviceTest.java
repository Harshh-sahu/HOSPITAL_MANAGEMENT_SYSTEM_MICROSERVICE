package com.hms.pharmacy.utility;

import com.hms.pharmacy.exception.HmsException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionControllerAdviceTest {

    @Test
    void exceptionHandler_returns500() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();

        var response = advice.exceptionHandler(new RuntimeException("x"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Some error Occured", response.getBody().getErrorMessage());
    }

    @Test
    void hmsExceptionHandler_usesEnvironmentMessage() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        Environment env = new MockEnvironment().withProperty("MEDICINE_NOT_FOUND", "Medicine not found");
        ReflectionTestUtils.setField(advice, "environment", env);

        var response = advice.HmsExceptionHandler(new HmsException("MEDICINE_NOT_FOUND"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Medicine not found", response.getBody().getErrorMessage());
    }

    @Test
    void handleValidationException_forConstraintViolation_returns400() {
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();

        var response = advice.handleValidationException(new ConstraintViolationException(Set.of()));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("", response.getBody().getErrorMessage());
    }
}

