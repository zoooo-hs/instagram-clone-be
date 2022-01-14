package com.zoooohs.instagramclone.domain.auth.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "refresh_token")
public class RefreshTokenEntity extends BaseEntity {
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "user_name", nullable = false)
    private String userName;
}
