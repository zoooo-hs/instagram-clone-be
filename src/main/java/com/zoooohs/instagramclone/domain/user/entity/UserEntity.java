package com.zoooohs.instagramclone.domain.user.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.common.type.AccountStatusType;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "user")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "user-info", attributeNodes = {
                @NamedAttributeNode("photo")
        })
})
@EqualsAndHashCode(of = {"name", "email"})
public class UserEntity extends BaseEntity implements UserDetails {

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 30)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Size(max = 300)
    @Column(name = "bio")
    private String bio;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    private PhotoEntity photo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatusType status = AccountStatusType.WAITING;

    @Builder
    public UserEntity(Long id, String email, String password, String name, String bio, PhotoEntity photo, AccountStatusType status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.photo = photo;
        this.status = status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
