package org.orderhub.pr.auth.controller;

import lombok.RequiredArgsConstructor;
import org.orderhub.common.MemberRole;
import org.orderhub.common.MemberStatus;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.dto.MemberQueryDto.FindMemberByIdResponse;
import org.orderhub.pr.auth.dto.MemberQueryDto.FindMemberByUsernameResponse;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.orderhub.pr.config.resolver.CurrentMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberQueryApi {
    private final MemberQueryService memberQueryService;

    @GetMapping
    public Page<FindMemberByIdResponse> findAllMembers(@PageableDefault(size = 20, sort = {"createdAt"},
            direction = Direction.DESC) Pageable pageable) {
        return memberQueryService.findAllMembers(pageable);
    }

    @GetMapping("/me")
    public Member getUsername(@CurrentMemberId UUID memberId) {
        return memberQueryService.findMemberEntityById(memberId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindMemberByIdResponse> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberQueryService.findMemberById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<FindMemberByUsernameResponse> getMemberByUsername(@PathVariable String username) {
        return ResponseEntity.ok(memberQueryService.findByUsername(username));
    }

    @GetMapping("/status/{status}")
    public Page<FindMemberByIdResponse> getMembersByStatus(@PathVariable MemberStatus status,
                                                           @PageableDefault(size = 20, sort = {"createdAt"},
                                                                   direction = Direction.DESC) Pageable pageable) {
        return memberQueryService.findByStatus(status, pageable);
    }

    @GetMapping("/role/{role}")
    public Page<FindMemberByIdResponse> getMembersByRole(@PathVariable MemberRole role,
                                                         @PageableDefault(size = 20, sort = {"createdAt"},
                                                                 direction = Direction.DESC) Pageable pageable) {
        return memberQueryService.findByRole(role,pageable);
    }

}

