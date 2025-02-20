package org.orderhub.pr.auth.controller;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.service.MemberService;
import org.springframework.web.bind.annotation.*;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberCommandApi {
    private final MemberService memberService;

    @PostMapping("/signup")
    public SignUpResponse signUp(@RequestBody SignUpRequest signUpRequest) {

        return memberService.signUp(signUpRequest);
    }

    @PutMapping("/{id}")
    public UpdateMemberRoleResponse updateUser(@PathVariable UUID id, @RequestBody MemberRole role) {
        // 구현 로직
        return  UpdateMemberRoleResponse.builder().build();
    }

    @DeleteMapping("/{id}")
    public DeleteMemberResponse deleteUser(@PathVariable UUID id) {
        // 구현 로직
        return DeleteMemberResponse.builder().build();
    }
}
