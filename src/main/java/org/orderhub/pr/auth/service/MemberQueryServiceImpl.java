package org.orderhub.pr.auth.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberQueryDto.*;
import org.orderhub.pr.auth.repository.MemberQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.orderhub.pr.system.exception.auth.ExceptionMessage.MEMBER_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    private final MemberQueryRepository memberQueryRepository;

    @Override
    public List<FindMemberByIdResponse> findAllMembers() {
        return memberQueryRepository.findAll().stream()
                .map(this::convertToFindMemberByIdResponse)
                .collect(Collectors.toList());

    }

    @Override
    public FindMemberByIdResponse findMemberById(UUID id) {
        Member member = memberQueryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));
        return convertToFindMemberByIdResponse(member);
    }

    @Override
    public FindMemberByUsernameResponse findByUsername(String username) {
        Member member = memberQueryRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));
        return convertToFindMemberByUsernameResponse(member);
    }

    @Override
    public Member findMemberEntityById(UUID id) {
        return memberQueryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));
    }

    @Override
    public List<FindMemberByIdResponse> findByStatus(MemberStatus status) {
        return memberQueryRepository.findByStatus(status)
                .stream()
                .map(this::convertToFindMemberByIdResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FindMemberByIdResponse> findByRole(MemberRole role) {
        return memberQueryRepository.findByRole(role)
                .stream()
                .map(this::convertToFindMemberByIdResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return memberQueryRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByTel(String tel) {
        return memberQueryRepository.existsByTel(tel);
    }

    private FindMemberByIdResponse convertToFindMemberByIdResponse(Member member) {
        return FindMemberByIdResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .realName(member.getRealName())
                .tel(member.getTel())
                .role(member.getRole())
                .status(member.getStatus())
                .build();
    }

    private FindMemberByUsernameResponse convertToFindMemberByUsernameResponse(Member member) {
        return FindMemberByUsernameResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .realName(member.getRealName())
                .tel(member.getTel())
                .role(member.getRole())
                .status(member.getStatus())
                .build();
    }

}
