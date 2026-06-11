package com.HMS_MCP.Hospital.Management.System.Model.context.Server.tools;

import com.HMS_MCP.Hospital.Management.System.Model.context.Server.config.HmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserTools {

    private final RestClient restClient;
    private final HmsProperties hmsProperties;

    @Tool(description = "Register a new user account in the hospital system. Roles: ADMIN, DOCTOR, PATIENT. Returns 'Account created.' on success.")
    public String registerUser(String name, String email, String password, String role) {
        try {
            return restClient.post()
                    .uri(hmsProperties.getUserServiceUrl() + "/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("name", name, "email", email, "password", password, "role", role))
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error registering user: " + e.getMessage();
        }
    }

    @Tool(description = "Authenticate a user with email and password. Returns a JWT token on success that must be used for subsequent secured requests.")
    public String loginUser(String email, String password) {
        try {
            return restClient.post()
                    .uri(hmsProperties.getUserServiceUrl() + "/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", email, "password", password))
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error during login: " + e.getMessage();
        }
    }

    @Tool(description = "Get the profile ID linked to a user account by user ID. Returns the doctor or patient profile ID associated with that user.")
    public String getUserProfileId(Long userId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getUserServiceUrl() + "/user/getProfile/" + userId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching user profile id: " + e.getMessage();
        }
    }

    @Tool(description = "Logout the current user session. Since HMS uses stateless JWT authentication, " +
            "this tool invalidates the session on the client side. " +
            "The provided JWT token should be discarded after calling this.")
    public String logoutUser(String jwtToken) {
        if (jwtToken == null || jwtToken.isBlank()) {
            return "{\"status\":\"error\",\"message\":\"No token provided.\"}";
        }
        return "{\"status\":\"success\",\"message\":\"Logged out successfully. Please discard your JWT token.\",\"token\":\"" + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...[invalidated]\"}";
    }

    @Tool(description = "Get monthly registration counts for doctors and patients. Returns month-wise registration trend data for analytics dashboards.")
    public String getMonthlyRegistrationCounts() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getUserServiceUrl() + "/user/getRegistrationCounts")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching registration counts: " + e.getMessage();
        }
    }
}
