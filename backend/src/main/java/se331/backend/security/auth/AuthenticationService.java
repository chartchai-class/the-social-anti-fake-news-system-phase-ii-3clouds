package se331.backend.rest.security.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se331.backend.rest.security.config.JwtService;
import se331.backend.rest.security.token.Token;
import se331.backend.rest.security.token.TokenRepository;
import se331.backend.rest.security.token.TokenType;
import se331.backend.rest.security.user.Role;
import se331.backend.rest.security.user.User;
import se331.backend.rest.security.user.UserRepository;
import se331.rest.util.LabMapper;

import se331.rest.entity.Organizer;
import se331.rest.repository.OrganizerRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrganizerRepository organizerRepository;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // 1. สร้าง Organizer
        Organizer organizer = Organizer.builder()
                .name(request.getName())
                .build();

        // 2. สร้าง User
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername()) // ใช้ username
                .roles(List.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        // 3. เชื่อมโยง User <-> Organizer (ก่อนบันทึก)
        user.setOrganizer(organizer);
        organizer.setUser(user); // ถ้ามี mappedBy ที่ถูกต้อง

        // 4. บันทึก Entity ที่เป็นเจ้าของความสัมพันธ์ (ถ้าไม่มี Cascade)
        // หรือบันทึกตามลำดับที่ปลอดภัย

        // บันทึก User ก่อน เพราะ Organizer มี FK ชี้มาที่ User
        // หรือบันทึก Organizer ก่อนก็ได้ ขึ้นอยู่กับว่าใครถือ @JoinColumn

        // วิธีที่ 1: บันทึก User ก่อน (ถ้า Organizer มี FK ชี้ไปที่ User)
        var savedUser = repository.save(user);
        organizerRepository.save(organizer);

        // 5. สร้าง Token และ Response
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                // เรียกใช้ DTO Mapper จาก savedUser เพื่อความปลอดภัย
                .user(LabMapper.INSTANCE.getOrganizerAuthDTO(savedUser.getOrganizer()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
//    revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(LabMapper.INSTANCE.getOrganizerAuthDTO(user.getOrganizer()))
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = this.repository.findByUsername(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
