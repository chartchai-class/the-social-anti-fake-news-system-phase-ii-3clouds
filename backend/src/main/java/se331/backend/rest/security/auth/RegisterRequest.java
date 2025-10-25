package se331.backend.rest.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String firstname;
  private String lastname;
  private String email;
  private String password;

  private String username; // เพิ่ม username เพราะ authenticate ใช้ username
  private String name; // เพิ่มสำหรับ Organizer
}
