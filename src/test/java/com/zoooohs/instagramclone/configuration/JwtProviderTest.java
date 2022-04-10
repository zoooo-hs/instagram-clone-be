package com.zoooohs.instagramclone.configuration;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
public class JwtProviderTest {

    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    ModelMapper modelMapper;


    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, modelMapper);
    }

    @DisplayName("Token claim key값을 type과 함께 가져오기")
    @Test
    void getValueTest() {
        String userId = "test@test.com";
        String actual = jwtTokenProvider.createAccessToken(userId);

        Assertions.assertEquals(userId, jwtTokenProvider.getAccessTokenValue(actual, "sub", String.class));
    }

    @DisplayName("Token안에 email, name, id, photo path까지 가져오기")
    @Test
    void tokenClaimTest() {
        UserDto.Info userDto = UserDto.Info.builder().id(1L).name("test").email("test@test.com").bio("hello").photo(PhotoDto.Photo.builder().id(1L).path("/logo512.png").build()).build();

        String actual = jwtTokenProvider.createAccessToken(userDto);

        Assertions.assertEquals(userDto.getName(), jwtTokenProvider.getAccessTokenValue(actual, "name", String.class));
        Assertions.assertEquals(userDto.getEmail(), jwtTokenProvider.getAccessTokenValue(actual, "email", String.class));
        Assertions.assertEquals(userDto.getPhoto(), jwtTokenProvider.getAccessTokenValue(actual, "photo", PhotoDto.Photo.class));
    }
}
