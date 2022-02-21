package com.zoooohs.instagramclone.configuration;

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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${instagram-clone.jwt.access-token.key}")
    private String refreshTokenKey = "refresh";
    @Value("${instagram-clone.jwt.refresh-token.key}")
    private String accessTokenKey = "access";

    @Value("${instagram-clone.jwt.access-token.valid-time}")
    private long accessTokenValidTime = 66400000;
    @Value("${instagram-clone.jwt.refresh-token.valid-time}")
    private long refreshTokenValidTime = 232000000;

    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;

    @PostConstruct
    protected void init() {
        refreshTokenKey = Base64.getEncoder().encodeToString(refreshTokenKey.getBytes());
        accessTokenKey = Base64.getEncoder().encodeToString(accessTokenKey.getBytes());
    }

    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenValidTime, refreshTokenKey);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenValidTime, accessTokenKey);
    }

    private String createToken(String userId, long accessTokenValidTime, String accessTokenKey) {
        Claims claims = Jwts.claims().setSubject(userId);
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date((now.toEpochMilli() + accessTokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, accessTokenKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getAccessTokenUserId(token));
        return new UsernamePasswordAuthenticationToken(this.modelMapper.map(userDetails, UserDto.class), "", userDetails.getAuthorities());
    }

    public String getAccessTokenUserId(String token) {
        return getUserIdFromToken(token, refreshTokenKey);
    }

    public String getRefreshTokenUserId(String token) {
        return getUserIdFromToken(token, accessTokenKey);
    }

    private String getUserIdFromToken(String token, String accessTokenKey) {
        return Jwts.parser().setSigningKey(accessTokenKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }

    public boolean validAccessToken(String jwtToken) {
        return isValidToken(jwtToken, refreshTokenKey);
    }

    public boolean validRefreshToken(String jwtToken) {
        return isValidToken(jwtToken, accessTokenKey);
    }

    private boolean isValidToken(String jwtToken, String refreshTokenKey) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(Date.from(Instant.now()));
        } catch (Exception e) {
            return false;
        }
    }


}
