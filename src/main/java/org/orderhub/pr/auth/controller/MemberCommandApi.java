package org.orderhub.pr.auth.controller;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
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

    @PutMapping("/members/{id}/role")
    public UpdateMemberRoleResponse updateUserRole(@PathVariable UUID id, @RequestBody MemberRole role) {
        // 구현 로직
        return  memberService.updateMemberRole(id, role);
    }

    @PutMapping("/members/{id}/status")
    public ChangeStatusResponse updateUserStatus(@PathVariable UUID id, @RequestBody MemberStatus status) {
        // 구현 로직
        return  memberService.changeMemberStatus(id, status);
    }

    @DeleteMapping("/members/{id}/delete")
    public DeleteMemberResponse deleteUser(@PathVariable UUID id) {
        // 구현 로직
        return memberService.deleteMember(id);
    }
}
