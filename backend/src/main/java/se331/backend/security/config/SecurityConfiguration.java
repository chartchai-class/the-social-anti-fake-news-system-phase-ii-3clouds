package se331.backend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CorsFilter;

import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.headers((headers) -> {
            headers.frameOptions((frameOptions) -> frameOptions.disable());
        });
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf((crsf) -> crsf.disable())
                .authorizeHttpRequests((authorize) -> {
                    authorize
                            .requestMatchers(HttpMethod.GET, "/api/v1/auth/check-username").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/auth/check-email").permitAll()

                            // 1. Auth API (Login/Register) - เปิดสาธารณะ
                            .requestMatchers("/api/v1/auth/**").permitAll()

                            // 2. อ่าน News (GET)
                            // removed จำเป็นต้องเป็น admin
                            .requestMatchers(HttpMethod.GET, "/api/news/removed").hasRole("ADMIN")
                            // เปิดสาธารณะสำหรับข่าวทั่วไป
                            .requestMatchers(HttpMethod.GET, "/api/news").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()

                            // 3. ดึงคอมเมนต์ (GET /api/comments/news/{newsId})
                            .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()

                            // 4. Upload Files - เปิดสาธารณะ (หรือกำหนด role ตามต้องการ)
                            .requestMatchers(HttpMethod.POST, "/uploadFile").permitAll()
                            .requestMatchers(HttpMethod.POST, "/uploadImage").permitAll()

                            // 5. โหวต/คอมเมนต์ (POST /api/news/{id}/comments)
                            // (READER, MEMBER, ADMIN ทำได้)
                            .requestMatchers(HttpMethod.POST, "/api/news/*/comments").hasAnyRole("READER", "MEMBER", "ADMIN")

                            // 6. โหวต/คอมเมนต์ (POST /api/comments)
                            .requestMatchers(HttpMethod.POST, "/api/comments").hasAnyRole("READER", "MEMBER", "ADMIN")

                            // 7. โพสต์ข่าวใหม่ (POST /api/news)
                            .requestMatchers(HttpMethod.POST, "/api/news").hasAnyRole("MEMBER", "ADMIN")

                            // 8. ลบข่าว/คอมเมนต์ (DELETE)
                            // (ADMIN ทำได้)
                            .requestMatchers(HttpMethod.DELETE, "/api/news/*/comments/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/news/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasRole("ADMIN")

                            // 9. จัดการ User
                            .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                            // 10. Request อื่นๆ ที่เหลือ - ต้อง Login
                            .anyRequest().authenticated();
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> {
                    logout.logoutUrl("/api/v1/auth/logout");
                    logout.addLogoutHandler(logoutHandler);
                    logout.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                })
        ;
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://13.212.6.216:8001"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("x-total-count"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        FilterRegistrationBean<CorsFilter> bean =
                new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
