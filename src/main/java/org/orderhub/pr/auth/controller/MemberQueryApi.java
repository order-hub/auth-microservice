package org.orderhub.pr.auth.controller;

import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.orderhub.pr.auth.exception.ExceptionMessage.MEMBER_NOT_FOUND_ERROR;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberQueryApi {
    private final MemberQueryService memberQueryService;

    @GetMapping
    public List<FindMemberByIdResponse> findAllMembers(){
        return memberQueryService.findAllMembers();
    }

    @GetMapping("/{id}")
    public Optional<FindMemberByIdResponse> getMemberById(@PathVariable UUID id) {
        return memberQueryService.findMemberById(id);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<FindMemberByUsernameResponse> getMemberByUsername(@PathVariable String username) {
        return memberQueryService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));
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

