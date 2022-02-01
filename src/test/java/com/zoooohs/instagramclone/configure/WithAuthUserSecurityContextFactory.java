package com.zoooohs.instagramclone.configure;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import org.apache.catalina.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {
    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        String email = annotation.email();
        Long id = annotation.id();

        UserDto userDto = UserDto.builder().id(id).email(email).build();
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDto, "passwd");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
