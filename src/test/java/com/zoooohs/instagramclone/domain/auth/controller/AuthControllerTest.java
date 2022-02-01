package com.zoooohs.instagramclone.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
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

        given(authService.signUp(any(AuthDto.SignUp.class))).willReturn(getToken());

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(signUpDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void singInTest() throws Exception {
        String url = "/auth/sign-in";

        Date now = new Date();
        String email = "sign-up-test-id"+now.getTime()+"@email.com";
        String password = "passwd";
        AuthDto.SignIn signIn = new AuthDto.SignIn();
        signIn.setEmail(email);
        signIn.setPassword(password);

        given(authService.signIn(any(AuthDto.SignIn.class))).willReturn(getToken());

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(signIn)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void refreshTest() throws Exception {
        String url = "/auth/refresh";
        AuthDto.Token token = getToken();

        given(authService.refresh(any(AuthDto.Token.class))).willReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(token)))
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
}
