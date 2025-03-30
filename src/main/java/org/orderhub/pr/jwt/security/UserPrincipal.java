package org.orderhub.pr.jwt.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Member member;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Member member) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
    }


    @Override
    public String getPassword() {
        return String.valueOf(member.getPassword());
    }

    @Override
    public String getUsername() {
        return String.valueOf(member.getId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Account non-expired 상태를 기본값으로 설정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Account non-locked 상태를 기본값으로 설정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Credentials non-expired 상태를 기본값으로 설정
    }

    @Override
    public boolean isEnabled() {
        return true;  // Enabled 상태를 기본값으로 설정
    }
}