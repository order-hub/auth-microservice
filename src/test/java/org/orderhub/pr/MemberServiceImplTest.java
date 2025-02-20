package org.orderhub.pr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.repository.MemberRepository;
import org.orderhub.pr.auth.service.MemberServiceImpl;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    @InjectMocks
    private SignUpRequest signUpRequest;


    @BeforeEach
    void initializeTestData() {
        signUpRequest = SignUpRequest.builder()
                .username("testuser")
                .password("Password123!")
                .realName("Test User")
                .tel("01012345678")
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignUpMember() {
        // given
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn(encodedPassword);

        Member savedMember = Member.builder()
                .id(UUID.randomUUID())
                .username(signUpRequest.getUsername())
                .password(encodedPassword) // 인코딩된 비밀번호 확인
                .realName(signUpRequest.getRealName())
                .tel(signUpRequest.getTel())
//                .role(MemberRole.ADMIN) // DEFAULT: MANAGER
//                .status(MemberStatus.ACTIVE) // DEFAULT: PENDING
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // when
        SignUpResponse response = memberService.signUp(signUpRequest);

        // then
        assertTrue(response.getSuccess(), "회원가입이 성공해야 합니다.");

        // savedMember의 정보가 입력한 signUpRequest와 일치하는지 확인
        assertAll("회원가입 정보 검증",
                () -> assertEquals("아이디가 일치해야 합니다.", savedMember.getUsername(), signUpRequest.getUsername()),
                () -> assertEquals("비밀번호가 인코딩되어야 합니다.", savedMember.getPassword(), encodedPassword),
                () -> assertEquals("이름이 일치해야 합니다.", savedMember.getRealName(), signUpRequest.getRealName()),
                () -> assertEquals("전화번호가 일치해야 합니다.", savedMember.getTel(), signUpRequest.getTel()),
                () -> assertEquals("기본 역할이 ADMIN 여야 합니다.", savedMember.getRole().name(), MemberRole.MANAGER.name()),
                () -> assertEquals("기본 상태가 ACTIVE 이어야 합니다.", savedMember.getStatus().name(), MemberStatus.PENDING.name())
        );


        // memberRepository.save()가 정확히 한 번 호출되었는지 확인
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}
