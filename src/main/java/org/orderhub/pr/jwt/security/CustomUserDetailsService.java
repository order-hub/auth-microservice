package org.orderhub.pr.jwt.security;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberQueryService memberQueryService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID memberId = UUID.fromString(username);
        Member member = memberQueryService.findMemberEntityById(memberId);

        return new UserPrincipal(member);
    }

}
