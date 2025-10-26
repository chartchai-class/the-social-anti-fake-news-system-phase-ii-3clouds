package se331.backend.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se331.backend.entity.UserAuthDTO;
import se331.backend.security.config.JwtService;
import se331.backend.security.token.Token;
import se331.backend.security.token.TokenRepository;
import se331.backend.security.token.TokenType;
import se331.backend.security.user.Role;
import se331.backend.security.user.User;
import se331.backend.security.user.UserRepository;
import se331.backend.util.NewsMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private NewsMapper newsMapper;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .profileImage(request.getProfileImage())
                .roles(List.of(Role.ROLE_READER)) // Role เริ่มต้นคือ READER                .enabled(true)
                .build();

        var savedUser = repository.save(user);

        var jwtToken = jwtService.generateToken(savedUser);
        saveUserToken(savedUser, jwtToken);

        UserAuthDTO userDto = newsMapper.toUserAuthDTO(savedUser);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .user(userDto)
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        saveUserToken(user, jwtToken);

        UserAuthDTO userDto = newsMapper.toUserAuthDTO(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .user(userDto)
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

}
