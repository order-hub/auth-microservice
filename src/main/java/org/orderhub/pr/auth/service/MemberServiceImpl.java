package org.orderhub.pr.auth.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.orderhub.pr.auth.dto.MemberQueryDto.*;
import org.orderhub.pr.auth.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.orderhub.pr.auth.exception.ExceptionMessage.MEMBER_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .realName(request.getRealName())
                .tel(request.getTel())
                .build();
        memberRepository.save(member);

        return SignUpResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public FindMemberResponse getAllMembers() {
        List<FindMemberByIdResponse> members = memberRepository.findAll().stream()
                .map(member -> FindMemberByIdResponse.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .realName(member.getRealName())
                        .tel(member.getTel())
                        .role(member.getRole())
                        .status(member.getStatus())
                        .success(true)
                        .build())
                .toList();

        return FindMemberResponse.builder()
                .members(members)
                .build();
    }

    @Override
    public FindMemberByIdResponse getMemberById(UUID id) {
        return memberRepository.findById(id)
                .map(member -> FindMemberByIdResponse.builder()
                        .username(member.getUsername())
                        .realName(member.getRealName())
                        .tel(member.getTel())
                        .role(member.getRole())
                        .status(member.getStatus())
                        .build())
                .orElse(FindMemberByIdResponse.builder().success(false).build());
    }


    @Override
    public FindMemberByUsernameResponse findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .map(member -> FindMemberByUsernameResponse.builder()
                        .username(member.getUsername())
                        .realName(member.getRealName())
                        .tel(member.getTel())
                        .role(member.getRole())
                        .status(member.getStatus())
                        .build())
                .orElse(FindMemberByUsernameResponse.builder().success(false).build());
    }

    @Override
    @Transactional
    public ChangeStatusResponse changeMemberStatus(UUID id, MemberStatus status) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        Member updatedMember = member.toBuilder().status(status).build();
        memberRepository.save(updatedMember);

        return ChangeStatusResponse.builder().success(true).build();
    }

    @Override
    @Transactional
    public UpdateMemberRoleResponse updateMemberRole(UUID id, MemberRole newRole) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        Member updatedMember = member.toBuilder().role(newRole).build();
        memberRepository.save(updatedMember);

        return UpdateMemberRoleResponse.builder().success(true).build();
    }

    @Override
    @Transactional
    public UpdatePasswordResponse updatePassword(UUID id, String newPassword) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        Member updatedMember = member.toBuilder()
                .password(passwordEncoder.encode(newPassword))
                .build();
        memberRepository.save(updatedMember);

        return UpdatePasswordResponse.builder().success(true).build();
    }

    @Override
    @Transactional
    public DeleteMemberResponse deleteMember(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        Member updatedMember = member.toBuilder().status(MemberStatus.DELETED).build();
        memberRepository.save(updatedMember);

        return DeleteMemberResponse.builder().success(true).build();
    }
}
