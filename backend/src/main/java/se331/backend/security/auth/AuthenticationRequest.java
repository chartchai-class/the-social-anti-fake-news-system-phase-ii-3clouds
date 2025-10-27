package se331.backend.security.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    /**
     * รองรับการส่ง "identifier", "username" หรือ "email"
     * ทั้งหมดจะถูก map มาเก็บในฟิลด์ identifier นี้
     */
    @JsonAlias({"username", "email"})
    private String identifier;

    private String password;
}
