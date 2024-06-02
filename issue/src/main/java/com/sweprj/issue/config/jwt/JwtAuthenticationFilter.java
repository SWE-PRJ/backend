package com.sweprj.issue.config.jwt;

import com.sweprj.issue.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /*
        JWT를 통해 인증된 사용자를 식별하는 필터.
        JWT를 통해 인증된 사용자가 요청을 보낼 때마다 JWT의 유효성을 검사하고, 유효한 JWT인 경우 SecurityContextHolder에 사용자 정보를 저장함.
     */

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String token = getTokenFromJwt(request);  // HTTP 요청에서 JWT 토큰 추출

            // JWT 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token) == JwtValidationType.VALID_JWT) {
                if (tokenBlacklist.contains(token)) {
                    throw new InvalidTokenException("유효한 JWT 토큰이 없습니다");
                }

                Long memberId = jwtTokenProvider.getUserFromJwt(token);
                String role = jwtTokenProvider.getRoleFromJwt(token);

                Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                // 사용자 인증 객체 생성
                UserAuthentication authentication = new UserAuthentication(memberId.toString(), token, authorities);

                // request 정보로 사용자 객체 디테일 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContextHolder에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(Exception exception){
            throw new InvalidTokenException("유효한 JWT 토큰이 없습니다");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }
}