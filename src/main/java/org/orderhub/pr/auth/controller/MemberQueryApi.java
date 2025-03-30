package org.orderhub.pr.auth.controller;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberQueryDto.FindMemberByIdResponse;
import org.orderhub.pr.auth.dto.MemberQueryDto.FindMemberByUsernameResponse;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberQueryApi {
    private final MemberQueryService memberQueryService;

    @GetMapping
    public List<FindMemberByIdResponse> findAllMembers(){
        return memberQueryService.findAllMembers();
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
    public List<FindMemberByIdResponse> getMembersByStatus(@PathVariable MemberStatus status) {
        return memberQueryService.findByStatus(status);
    }

    @GetMapping("/role/{role}")
    public List<FindMemberByIdResponse> getMembersByRole(@PathVariable MemberRole role) {
        return memberQueryService.findByRole(role);
    }

}

