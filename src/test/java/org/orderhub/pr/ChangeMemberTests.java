package org.orderhub.pr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.orderhub.pr.auth.repository.MemberCommandRepository;
import org.orderhub.pr.auth.service.MemberCommandServiceImpl;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChangeMemberTests {

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberQueryService memberQueryService;

    @InjectMocks
    private MemberCommandServiceImpl memberCommandService;

    private UUID memberId;
    private Member existingMember;

    @BeforeEach
    void setUpMember() {
        memberId = UUID.randomUUID();
        existingMember = Member.builder()
                .id(memberId)
                .username("existingUser")
                .password("encodedPassword")
                .realName("Existing User")
                .tel("01099998888")
                .role(MemberRole.MANAGER)
                .status(MemberStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("기존 회원의 상태 변경 테스트")
    void testUpdateMemberStatus() {

        // memberQueryService에서 findMemberById가 기존 회원을 반환하도록 설정
        when(memberQueryService.findMemberEntityById(memberId)).thenReturn(existingMember);

        // 상태 변경 요청
        MemberStatus newStatus = MemberStatus.ACTIVE;

        // when
        memberCommandService.changeMemberStatus(memberId, newStatus);

        // then
        assertEquals("회원 상태가 변경되어야 합니다.", newStatus.name(), existingMember.getStatus().name());

        // memberCommandRepository.save 호출 여부 확인
        verify(memberCommandRepository, times(1)).save(existingMember);
    }

    @Test
    @DisplayName("패스워드 변경 테스트")
    void testChangePasswordSuccess() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("OldPassword123!", "NewPassword456!");

        // when
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedPassword");
        existingMember = Member.builder()
                .id(memberId)
                .username("testUser")
                .password("encodedOldPassword")
                .realName("Test User")
                .tel("01012345678")
                .role(MemberRole.MANAGER)
                .status(MemberStatus.ACTIVE)
                .build();

        when(memberQueryService.findMemberEntityById(memberId)).thenReturn(existingMember);
        when(passwordEncoder.matches(request.getCurrentPassword(), existingMember.getPassword())).thenReturn(true);  // 평문과 암호화된 값 비교
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedPassword2");

        // 비밀번호 변경 작업 수행
        memberCommandService.updatePassword(memberId, request);

        // then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberCommandRepository, times(1)).save(memberCaptor.capture()); // save() 호출 검증

        Member updatedMember = memberCaptor.getValue();

        // 암호화된 값이 일치하는지 확인
        assertEquals("비밀번호가 동일해야합니다.", "encodedPassword2", updatedMember.getPassword());
    }

    @Test
    @DisplayName("기존 회원 논리삭제")
    void testDeleteMember() {
        // when
        when(memberQueryService.findMemberEntityById(memberId)).thenReturn(existingMember);
        memberCommandService.deleteMember(memberId);

        // then
        assertEquals("회원 상태가 변경되어야 합니다.", MemberStatus.DELETED.name(), existingMember.getStatus().name());
    }
}
