package com.zoooohs.instagramclone.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto.Token;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${instagram-clone.jwt.refresh-token.key}")
    private String refreshTokenKey = "refresh";
    @Value("${instagram-clone.jwt.access-token.key}")
    private String accessTokenKey = "access";

    @Value("${instagram-clone.jwt.access-token.valid-time}")
    private long accessTokenValidTime = 66400000;
    @Value("${instagram-clone.jwt.refresh-token.valid-time}")
    private long refreshTokenValidTime = 232000000;

    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    protected void init() {
        refreshTokenKey = Base64.getEncoder().encodeToString(refreshTokenKey.getBytes());
        accessTokenKey = Base64.getEncoder().encodeToString(accessTokenKey.getBytes());
    }

    public Token createToken(Long id) {
        String accessToken = createToken(id, accessTokenValidTime, accessTokenKey);
        String refreshToken = createToken(id, refreshTokenValidTime, refreshTokenKey);
        return Token.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public String createAccessToken(UserDto.Info userDto) {
        return createToken(userDto, accessTokenValidTime, accessTokenKey);
    }

    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenValidTime, accessTokenKey);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenValidTime, refreshTokenKey);
    }

    private String createToken(UserDto.Info userDto, long tokenValidTime, String signKey) {
        Claims claims = Jwts.claims().setSubject(userDto.getEmail());
        claims.put("email", userDto.getEmail());
        claims.put("name", userDto.getName());
        claims.put("bio", userDto.getBio());
        claims.put("photo", userDto.getPhoto());
        claims.put("id", userDto.getId());
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date((now.toEpochMilli() + tokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, signKey)
                .compact();
    }

    private String createToken(String userId, long tokenValidTime, String signKey) {
        Claims claims = Jwts.claims().setSubject(userId);
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date((now.toEpochMilli() + tokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, signKey)
                .compact();
    }

    private String createToken(Long userId, long tokenValidTime, String signKey) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date((now.toEpochMilli() + tokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, signKey)
                .compact();
    }
    
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getAccessTokenUserId(token));
        return new UsernamePasswordAuthenticationToken(this.modelMapper.map(userDetails, UserDto.class), "", userDetails.getAuthorities());
    }

    public String getAccessTokenUserId(String token) {
        return getUserIdFromToken(token, accessTokenKey);
    }

    public String getRefreshTokenUserId(String token) {
        return getUserIdFromToken(token, refreshTokenKey);
    }

    private String getUserIdFromToken(String token, String signKey) {
        return Jwts.parser().setSigningKey(signKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }

    public boolean validAccessToken(String jwtToken) {
        return isValidToken(jwtToken, accessTokenKey);
    }

    public boolean validRefreshToken(String jwtToken) {
        return isValidToken(jwtToken, refreshTokenKey);
    }

    private boolean isValidToken(String jwtToken, String signKey) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(signKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(Date.from(Instant.now()));
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T getValue(String token, String key, Class<T> classType) {
        if (classType == String.class) {
            return Jwts.parser().setSigningKey(accessTokenKey).parseClaimsJws(token).getBody().get(key, classType);
        }
        return objectMapper.convertValue(
                Jwts.parser().setSigningKey(accessTokenKey).parseClaimsJws(token).getBody().get(key, LinkedHashMap.class),
                classType);
    }
}
