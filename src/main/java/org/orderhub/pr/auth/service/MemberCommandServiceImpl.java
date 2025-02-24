package org.orderhub.pr.auth.service;

import jakarta.persistence.EntityNotFoundException;
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
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberCommandRepository memberCommandRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (memberCommandRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(DUPLICATE_USERNAME_ERROR);
        }
        if (memberCommandRepository.existsByTel(request.getTel())) {
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
    @Transactional
    public ChangeStatusResponse changeMemberStatus(UUID id, MemberStatus newStatus) {
        Member member = memberCommandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        member.updateMemberStatus(newStatus);
        memberCommandRepository.save(member);

        return ChangeStatusResponse.builder().success(true).build();
    }

    @Override
    @Transactional
    public UpdateMemberRoleResponse updateMemberRole(UUID id, MemberRole newRole) {
        Member member = memberCommandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        member.updateMemberRole(newRole);
        memberCommandRepository.save(member);

        return UpdateMemberRoleResponse.builder().success(true).build();
    }

    @Override
    @Transactional
    public UpdatePasswordResponse updatePassword(UUID id, UpdatePasswordRequest request) {
        Member member = memberCommandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updateMemberPassword(encodedPassword);
        memberCommandRepository.save(member);

        return UpdatePasswordResponse.builder().success(true).build();
    }

    @Override
    @Transactional
    public DeleteMemberResponse deleteMember(UUID id) {
        Member member = memberCommandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        member.deleteMember();
        memberCommandRepository.save(member);

        return DeleteMemberResponse.builder().success(true).build();
    }
}
