package org.orderhub.pr.auth.service;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.aop.annotation.AdminOnly;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.common.dto.KafkaDto.*;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.orderhub.pr.system.exception.auth.InvalidPasswordException;
import org.orderhub.pr.auth.repository.MemberCommandRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static org.orderhub.pr.system.exception.auth.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberCommandRepository memberCommandRepository;
    private final MemberQueryService memberQueryService;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

        Member savedMember = memberCommandRepository.save(member);

        String topic = "sign-up-topic";
        SignUpSendMessage message = SignUpSendMessage.builder()
                .memberId(savedMember.getId())
                .username(savedMember.getUsername())
                .password(savedMember.getPassword())
                .realName(savedMember.getRealName())
                .tel(savedMember.getTel())
                .role(savedMember.getRole())
                .status(savedMember.getStatus())
                .createdAt(savedMember.getCreatedAt())
                .build();

        kafkaTemplate.send(topic, message);

        return SignUpResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @AdminOnly
    public UpdateMemberStatusResponse updateMemberStatus(UUID id, UpdateMemberStatusRequest request) {
        Member targetMember = memberQueryService.findMemberEntityById(id);
        targetMember.updateMemberStatus(request.getStatus());

        String topic = "update-member-status-topic";
        UpdateStatusSendMessage message = UpdateStatusSendMessage.builder()
                .memberId(targetMember.getId())
                .status(request.getStatus())
                .build();

        kafkaTemplate.send(topic, message);
        return UpdateMemberStatusResponse.builder().success(true).build();
    }

    @Override
    @AdminOnly
    public UpdateMemberRoleResponse updateMemberRole(UUID id, UpdateMemberRoleRequest request) {
        Member targetMember = memberQueryService.findMemberEntityById(request.getTargetId());
        targetMember.updateMemberRole(request.getRole());

        String topic = "update-member-role-topic";
        UpdateRoleSendMessage message = UpdateRoleSendMessage.builder()
                .memberId(targetMember.getId())
                .role(request.getRole())
                .build();

        kafkaTemplate.send(topic, message);

        return UpdateMemberRoleResponse.builder().success(true).build();
    }

    @Override
    public UpdatePasswordResponse updatePassword(UUID id, UpdatePasswordRequest request) {
        Member member = memberQueryService.findMemberEntityById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new InvalidPasswordException(INVALID_CHK_PASSWORD_NEW_PASSWORD_ERROR);
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updateMemberPassword(encodedPassword);

        String topic = "update-password-topic";
        UpdatePasswordSendMessage message = UpdatePasswordSendMessage.builder()
                .memberId(member.getId())
                .password(encodedPassword)
                .build();

        kafkaTemplate.send(topic, message);

        return UpdatePasswordResponse.builder().success(true).build();
    }

    @Override
    public DeleteMemberResponse deleteMember(UUID id) {
        Member member = memberQueryService.findMemberEntityById(id);
        member.deleteMember();

        String topic = "delete-member-topic";
        DeleteMemberSendMessage message = DeleteMemberSendMessage.builder()
                .memberId(member.getId())
                .build();

        kafkaTemplate.send(topic, message);

        return DeleteMemberResponse.builder().success(true).build();
    }
}
