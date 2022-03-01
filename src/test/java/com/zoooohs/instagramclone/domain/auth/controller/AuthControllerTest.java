package com.zoooohs.instagramclone.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.service.AuthService;
import com.zoooohs.instagramclone.domain.service.MailService;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Base64;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(controllers = AuthController.class)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Spy
    ModelMapper modelMapper;
    ObjectMapper objectMapper;

    @MockBean
    AuthenticationManager authenticationManagerBean;
    @MockBean
    JwtTokenProvider jwtTokenProvider;
    @MockBean
    AuthService authService;

    @MockBean
    MailService mailService;

    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Test
    public void signUpTest() throws Exception {
        String url = "/auth/sign-up";

        Date now = new Date();
        String email = "sign-up-test-id"+now.getTime()+"@email.com";
        String password = "passwd";
        String name = "sign-up-test-name"+now.getTime();
        AuthDto.SignUp signUpDto = new AuthDto.SignUp();
        signUpDto.setEmail(email);
        signUpDto.setPassword(password);
        signUpDto.setName(name);

        given(authService.signUp(any(AuthDto.SignUp.class))).willReturn(Base64.getEncoder().encode((signUpDto.getEmail()+signUpDto.getName()).getBytes()).toString());

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .header("origin", "localhost:8080")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("OK")));
        verify(mailService, times(1)).send(anyString(), anyString(), anyString());
    }

    @Test
    public void singInTest() throws Exception {
        String url = "/auth/sign-in";

        Date now = new Date();
        String email = "sign-up-test-id"+now.getTime()+"@email.com";
        String password = "passwd";
        AuthDto.SignIn signIn = AuthDto.SignIn.builder().email(email).password(password).build();
        AuthDto.SignIn signIn401 = AuthDto.SignIn.builder().email("1"+email).password(password).build();

        given(authService.signIn(eq(signIn))).willReturn(getToken());
        given(authService.signIn(eq(signIn401))).willThrow(new ZooooException(ErrorCode.USER_NOT_VERIFIED));

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signIn)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signIn401)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void refreshTest() throws Exception {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        String url = "/auth/refresh";
        AuthDto.Token token = getToken();

        given(authService.refresh(any(AuthDto.Token.class))).willReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(token)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void checkNameTest() throws Exception {
        String url = "/auth/name";

        given(authService.checkDuplicatedName(anyString())).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .queryParam("keyword", "name2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void checkEmailTest() throws Exception {
        String url = "/auth/email";

        given(authService.checkDuplicatedEmail(anyString())).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .queryParam("keyword", "test@test.test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private AuthDto.Token getToken() {
        return AuthDto.Token.builder().accessToken("a.b.c").refreshToken("a.b.c").build();
    }

    @DisplayName("GET /auth/verification?email=&token= 토큰 맞으면 true, 아니면 404")
    @Test
    public void verificationTest() throws Exception {
        String url = "/auth/verification";

        given(authService.verification(eq("test@test.com"), anyString())).willAnswer(new Answer<Boolean>() {
            private int count = 0;
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                if (count > 0) {
                    throw new ZooooException(ErrorCode.ALREADY_VERIFIED);
                }
                count++;
                return true;
            }
        });
        given(authService.verification(eq("test@test1.com"), anyString())).willThrow(new ZooooException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("email", "test@test.com")
                        .queryParam("token", passwordEncoder.encode("test@test.com"+"test"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("email", "test@test1.com")
                        .queryParam("token", passwordEncoder.encode("test@test.com"+"test"))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("email", "test@test.com")
                        .queryParam("token", passwordEncoder.encode("test@test.com"+"test"))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
