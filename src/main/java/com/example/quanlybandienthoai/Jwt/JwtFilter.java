package com.example.quanlybandienthoai.Jwt;

import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.InvalidatedTokenRepository;
import com.example.quanlybandienthoai.service.Implement.CustomUserDetailsService;
import com.example.quanlybandienthoai.service.RedisService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;
    @Autowired
    private RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        // get token từ header Authorization và get username từ token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.getUserNameFormToken(token);
        }
        // Spring lưu user đã xác thực trong SecurityContextHolder
        // Nếu có username và chưa có user nào được lưu trong SecurityContextHolder thì sẽ thực hiện
        // xác thực người dùng thủ công và gán người dùng đó vào SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            JWTClaimsSet claimsSet = jwtUtil.getClaimsFromToken(token);
            String jwtId = claimsSet.getJWTID();
            // Kiểm tra token bị thu hồi trong Redis
            String redisKey = "blacklist:" + jwtId;
            Object isBlacklisted = redisService.getValue(redisKey);
            if (isBlacklisted != null) {
                throw new AppException(
                        DefinitionCode.UNAUTHORIZED,
                        "Token vô hiệu",
                        "Token has been invalidated (user logged out)");
            }

            boolean isValid = jwtUtil.validateToken(token, userDetails);
            // Khi token hợp lệ thì xác thực người dùng thủ công và gán người dùng đó vào SecurityContext
            if (isValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}
