package se331.backend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                            // 1. Auth API (Login/Register) - เปิดสาธารณะ
                            .requestMatchers("/api/v1/auth/**").permitAll()

                            // 2. อ่าน News (GET) - เปิดสาธารณะ
                            .requestMatchers(HttpMethod.GET, "/api/news").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()

                            // 3. โหวต/คอมเมนต์ (POST /api/news/{id}/comments)
                            // ✅ แก้จาก **/comments เป็น */comments
                            .requestMatchers(HttpMethod.POST, "/api/news/*/comments").hasAnyAuthority("ROLE_READER", "ROLE_MEMBER", "ROLE_ADMIN")

                            // ✅ เพิ่ม API สำหรับ CommentController
                            .requestMatchers(HttpMethod.POST, "/api/comments").permitAll()  // หรือเปลี่ยนเป็น hasAnyAuthority ถ้าต้องการให้ต้อง login
                            .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()

                            // 4. โพสต์ข่าวใหม่ (POST /api/news)
                            // (MEMBER, ADMIN ทำได้)
                            .requestMatchers(HttpMethod.POST, "/api/news").hasAnyAuthority("ROLE_MEMBER", "ROLE_ADMIN")

                            // 5. ลบข่าว/คอมเมนต์ (DELETE)
                            // (ADMIN ทำได้)
                            .requestMatchers(HttpMethod.DELETE, "/api/news/**").hasAuthority("ROLE_ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasAuthority("ROLE_ADMIN")

                            // 6. จัดการ User
                            // (ADMIN ทำได้)
                            .requestMatchers("/api/v1/users/**").hasAuthority("ROLE_ADMIN")

                            // 7. Request อื่นๆ ที่เหลือ - ต้อง Login
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