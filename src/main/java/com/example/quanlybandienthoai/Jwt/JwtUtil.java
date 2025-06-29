package com.example.quanlybandienthoai.Jwt;

import com.example.quanlybandienthoai.dto.Request.RefreshTokenRequest;
import com.example.quanlybandienthoai.dto.Response.RefreshTokenResponse;
import com.example.quanlybandienthoai.entity.InvalidatedToken;
import com.example.quanlybandienthoai.entity.User;
import com.example.quanlybandienthoai.repository.InvalidatedTokenRepository;
import com.example.quanlybandienthoai.repository.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class JwtUtil{
    @NonFinal
    @Value("${security.signer-key}")
    private String SIGNER_KEY;
    @Value("${jwt.valid-time}")
    private int EXPIRATION_TIME;
    @Value("${jwt.refresh-time}")
    private int REFRESH_TIME;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;
    public String generateToken(String username) {
        String token = null;
        try {
            JWSSigner signer = new MACSigner(SIGNER_KEY);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, EXPIRATION_TIME);
            var expirationTime = calendar.getTime();
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim("username", username);
            builder.issueTime(new Date());
            builder.jwtID(UUID.randomUUID().toString());
            builder.expirationTime(expirationTime);
            builder.claim("roles", buildScope(username));
            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
            signedJWT.sign(signer);
            token = signedJWT.serialize();

        }catch(Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public String generateRefreshToken(String username) {
        String refreshToken = null;
        try {
            JWSSigner signer = new MACSigner(SIGNER_KEY);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, REFRESH_TIME);
            var expirationRefreshTime = calendar.getTime();
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim("username", username);
            builder.issueTime(new Date());
            builder.expirationTime(expirationRefreshTime);
            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
            signedJWT.sign(signer);
            refreshToken = signedJWT.serialize();

        }catch(Exception e) {
            e.printStackTrace();
        }
        return refreshToken;
    }

    public JWTClaimsSet getClaimsFromToken(String token) {
        JWTClaimsSet claim = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY);
            if(signedJWT.verify(jwsVerifier)) {
                claim = signedJWT.getJWTClaimsSet();
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return claim;

    }


//    public String getJwtIdFromToken(String token){
//        String jwtId = null;
//        try{
//            JWTClaimsSet claims = getClaimsFromToken(token);
//            jwtId = claims.getJWTID();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return jwtId;
//    }

    public String getUserNameFormToken(String token) {
        String username = null;
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            username = claims.getStringClaim("username");

        }catch(Exception e) {
            e.printStackTrace();
        }
        return username;

    }
    public Date getExpirationFromToken(String token) {
        Date expiration = null;
        JWTClaimsSet claims = getClaimsFromToken(token);
        expiration = claims.getExpirationTime();
        return expiration;
    }

    public Boolean isTokenExpired(String token) {

        Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateTokenLogin(String token) {
        if(token==null || token.trim().length() == 0) {
            return false;
        }
        String username = getUserNameFormToken(token);
        if(username==null || username.isEmpty()) {
            return false;
        }
        if(isTokenExpired(token)) {
            return false;
        }
        return true;
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        return getUserNameFormToken(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
    public String buildScope(String username) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        User user = userRepository.findByUsername(username);
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });

        return stringJoiner.toString();
    }
}
