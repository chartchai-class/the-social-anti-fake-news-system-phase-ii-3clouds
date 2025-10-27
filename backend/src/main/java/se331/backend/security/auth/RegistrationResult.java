package se331.backend.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResult {
    private boolean success;
    private String message; // ข้อความ (Error หรือ Success)
    private AuthenticationResponse authenticationResponse; // ข้อมูล response (ถ้าสำเร็จ)

    public static RegistrationResult success(AuthenticationResponse response) {
        return RegistrationResult.builder()
                .success(true)
                .message("Registration successful")
                .authenticationResponse(response)
                .build();
    }

    public static RegistrationResult failure(String message) {
        return RegistrationResult.builder()
                .success(false)
                .message(message)
                .authenticationResponse(null)
                .build();
    }
}