package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.Jwt.JwtUtil;
import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.RefreshTokenRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.RefreshTokenResponse;
import com.example.quanlybandienthoai.entity.InvalidatedToken;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.InvalidatedTokenRepository;
import com.example.quanlybandienthoai.service.AuthService;
import com.example.quanlybandienthoai.service.RedisService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthServiceIplm implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    public LoginResponse login(LoginRequest request) throws ParseException {
        // Thực hiện xác thực username/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        // Lấy UserDetails từ xác thực thành công
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Sinh JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
        JWTClaimsSet claimsSet = jwtUtil.getClaimsFromToken(token);
        var response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        String rolesString = claimsSet.getStringClaim("roles");
        List<String> roles = Arrays.asList(rolesString.split(" "));
        response.setRoles(roles);
        return response;
    }

    @Override
    public void logout(LogoutRequest request) {
        JWTClaimsSet claimsSet = jwtUtil.getClaimsFromToken(request.getToken());
        // Lưu token vào Redis blacklist với TTL bằng thời gian còn lại
        String jti = claimsSet.getJWTID();
        long ttlSeconds = (claimsSet.getExpirationTime().getTime() - System.currentTimeMillis()) / 1000;
        if (ttlSeconds > 0) {
            String redisKey = "blacklist:" + jti;
            redisService.setValue(redisKey, "1", ttlSeconds);
        }
        // InvalidatedToken invalidatedToken = new InvalidatedToken();
        // invalidatedToken.setExpTime(claimsSet.getExpirationTime());
        // invalidatedToken.setUUID(jti);
        // invalidatedTokenRepository.save(invalidatedToken);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        RefreshTokenResponse response = new RefreshTokenResponse();
        try {
            var claims = jwtUtil.getClaimsFromToken(refreshToken);
            if (claims == null || jwtUtil.isTokenExpired(refreshToken)) {
                throw new AppException(
                        DefinitionCode.UNAUTHORIZED,
                        "Refresh token đã hết hạn hoặc không hợp lệ",
                        "Refresh token is expired or invalid");
            }
            String username = claims.getStringClaim("username");
            if (username == null || username.isEmpty()) {
                throw new AppException(
                        DefinitionCode.UNAUTHORIZED,
                        "Refresh token không hợp lệ",
                        "Refresh token is invalid");
            }
            JWTClaimsSet claimsSet = jwtUtil.getClaimsFromToken(request.getToken());
            String jwtId = claimsSet.getJWTID();
            long ttlSeconds = (claimsSet.getExpirationTime().getTime() - System.currentTimeMillis()) / 1000;
            if (ttlSeconds > 0) {
                String redisKey = "blacklist:" + jwtId;
                redisService.setValue(redisKey, "1", ttlSeconds);
            }
//            InvalidatedToken invalidatedToken = new InvalidatedToken();
//            invalidatedToken.setExpTime(claimsSet.getExpirationTime());
//            invalidatedToken.setUUID(claimsSet.getJWTID());
//            invalidatedTokenRepository.save(invalidatedToken);
            // Sinh access token mới
            String newToken = jwtUtil.generateToken(username);
            response.setToken(newToken);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setToken(null);
            return response;
        }
    }

}
