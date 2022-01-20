package com.zoooohs.instagramclone.configuration;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    // TODO: key 환경 변수화 혹은 은닉 + 주기적 변경
    private String accessTokenKey = "access_key_secret";
    private String refreshTokenKey = "refresh_key_secret";

    // 10분
    private long accessTokenValidTime = 10*60*1000;
    // 2일
    private long refreshTokenValidTime = 2*24*60*60*1000;

    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;

    @PostConstruct
    protected void init() {
        accessTokenKey = Base64.getEncoder().encodeToString(accessTokenKey.getBytes());
        refreshTokenKey = Base64.getEncoder().encodeToString(refreshTokenKey.getBytes());
    }


    public String createAccessToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date((now.getTime() + accessTokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, accessTokenKey)
                .compact();
    }
    public String createRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date((now.getTime() + refreshTokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, refreshTokenKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getAccessTokenUserId(token));
        return new UsernamePasswordAuthenticationToken(this.modelMapper.map(userDetails, UserDto.class), "", userDetails.getAuthorities());
    }

    public String getAccessTokenUserId(String token) {
        return Jwts.parser().setSigningKey(accessTokenKey).parseClaimsJws(token).getBody().getSubject();
    }
    public String getRefreshTokenUserId(String token) {
        return Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring("Bearer ".length());
    }

    public boolean validAccessToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(accessTokenKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validRefreshToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


}
