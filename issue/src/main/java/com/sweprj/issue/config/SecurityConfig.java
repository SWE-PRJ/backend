package com.sweprj.issue.config;

import com.sweprj.issue.config.jwt.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean   // 특정 HTTP 요청에 대한 웹 기반 보안 구성. (인증인가, 로그인, 로그아웃 설정)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and()
                .csrf().disable() // 실제 운영 환경에서는 활성화하는 걸 권장
                .httpBasic().disable() // HTTP 기본 인증 비활성화(사용자 이름과 비밀번호를 평문으로 전송하기 때문에 보안에 취약)

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 인증되지 않은 사용자의 접근에 대해 401 Unauthorized 에러를 리턴하는 클래스
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
//                .authorizeRequests()
                .authorizeHttpRequests(authorize -> authorize
//                .requestMatchers(new AntPathRequestMatcher("/admin/**")).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/projects/*/issues").hasAnyRole("ADMIN", "TESTER")
                        .requestMatchers(HttpMethod.POST, "/api/issues/*/comments").hasAnyRole("ADMIN", "TESTER", "DEV", "PL")
                        .requestMatchers(HttpMethod.GET, "/api/projects/*/issues").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projects/*/developers/*/issues").hasAnyRole("ADMIN", "DEV")
                        .requestMatchers(HttpMethod.GET, "/api/projects/*/tester/*/issues").hasAnyRole("ADMIN", "TESTER")
                        .requestMatchers(HttpMethod.PATCH, "/api/projects/*/issues/*").hasAnyRole("ADMIN", "PL", "DEV", "TESTER")
                        .requestMatchers(HttpMethod.POST, "/api/issues/*").hasAnyRole("ADMIN", "PL")
                        .requestMatchers(HttpMethod.POST, "/api/projects/{projectId}/{identifier}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/issues/{issueId}").hasAnyRole("ADMIN","TESTER", "PL")
//                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

//        return http
//                .csrf(AbstractHttpConfigurer::disable)  // 실제 운영 환경에서는 활성화하는 걸 권장
//                .httpBasic(AbstractHttpConfigurer::disable) // HTTP 기본 인증 비활성화(사용자 이름과 비밀번호를 평문으로 전송하기 때문에 보안에 취약)
//
//                // 세션을 사용하지 않기 때문에 STATELESS로 설정
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // 인증되지 않은 사용자의 접근에 대해 401 Unauthorized 에러를 리턴하는 클래스
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
//
//                //api 경로
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
////                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()) // 나머지 경로는 jwt 인증 해야함
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT를 통해 인증된 사용자를 식별하는 필터
//                .build();
    }

    // CORS 설정
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*");
            }
        };
    }

}