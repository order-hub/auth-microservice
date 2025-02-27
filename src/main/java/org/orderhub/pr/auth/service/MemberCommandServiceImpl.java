package org.orderhub.pr.auth.service;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.orderhub.pr.auth.repository.MemberCommandRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.orderhub.pr.auth.exception.ExceptionMessage.*;

// TODO 권한별 로직 추가
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberCommandRepository memberCommandRepository;
    private final MemberQueryService memberQueryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        if (memberQueryService.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(DUPLICATE_USERNAME_ERROR);
        }
        if (memberQueryService.existsByTel(request.getTel())) {
            throw new IllegalArgumentException(DUPLICATE_TEL_ERROR);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .realName(request.getRealName())
                .tel(request.getTel())
                .build();

        memberCommandRepository.save(member);

        return SignUpResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public UpdateStatusResponse updateMemberStatus(UUID id, MemberStatus newStatus) {
        Member member = memberQueryService.findMemberEntityById(id);
        member.updateMemberStatus(newStatus);

        return UpdateStatusResponse.builder().success(true).build();
    }

    @Override
    public UpdateMemberRoleResponse updateMemberRole(UUID id, MemberRole newRole) {
        Member member = memberQueryService.findMemberEntityById(id);
        member.updateMemberRole(newRole);

        return UpdateMemberRoleResponse.builder().success(true).build();
    }

    @Override
    public UpdatePasswordResponse updatePassword(UUID id, UpdatePasswordRequest request) {
        Member member = memberQueryService.findMemberEntityById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException(INVALID_PASSWORD_ERROR);
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updateMemberPassword(encodedPassword);
        memberCommandRepository.save(member);

        return UpdatePasswordResponse.builder().success(true).build();
    }

    @Override
    public DeleteMemberResponse deleteMember(UUID id) {
        Member member = memberQueryService.findMemberEntityById(id);

        member.deleteMember();
        memberCommandRepository.save(member);

        return DeleteMemberResponse.builder().success(true).build();
    }
}
