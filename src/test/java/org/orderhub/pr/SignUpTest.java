package org.orderhub.pr;

import org.junit.jupiter.api.Assertions;
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
import org.orderhub.pr.auth.repository.MemberCommandRepository;
import org.orderhub.pr.auth.service.MemberCommandServiceImpl;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.orderhub.pr.system.exception.auth.ExceptionMessage.DUPLICATE_TEL_ERROR;
import static org.orderhub.pr.system.exception.auth.ExceptionMessage.DUPLICATE_USERNAME_ERROR;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SignUpTest {

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberCommandServiceImpl memberService;

    @Mock
    private MemberQueryService memberQueryService;

    @InjectMocks
    private SignUpRequest signUpRequest;

    @BeforeEach
    void initializeTestData() {
        signUpRequest = SignUpRequest.builder()
                .username("tester")
                .password("Password123!")
                .realName("Test User")
                .tel("01012345678")
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignUpMember() {
        /*
        진짜로 암호화 된 코드 확인을 위해 작성했으나 확인 후 주석처리
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        */
        // given
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

        Member savedMember = Member.builder()
                .id(UUID.randomUUID())
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword())) // 인코딩된 비밀번호 확인
                .realName(signUpRequest.getRealName())
                .tel(signUpRequest.getTel())
//                .role(MemberRole.ADMIN) // DEFAULT: MANAGER
//                .status(MemberStatus.ACTIVE) // DEFAULT: PENDING
                .build();

        when(memberCommandRepository.save(any(Member.class))).thenReturn(savedMember);

        // when
        SignUpResponse response = memberService.signUp(signUpRequest);

        // then
        assertTrue(response.getSuccess(), "회원가입이 성공해야 합니다.");

        // savedMember의 정보가 입력한 signUpRequest와 일치하는지 확인
        assertAll("회원가입 정보 검증",
                () -> assertEquals("아이디가 일치해야 합니다.", savedMember.getUsername(), signUpRequest.getUsername()),
                () -> assertEquals("비밀번호가 인코딩되어야 합니다.", savedMember.getPassword(), passwordEncoder.encode(signUpRequest.getPassword())),
                () -> assertEquals("이름이 일치해야 합니다.", savedMember.getRealName(), signUpRequest.getRealName()),
                () -> assertEquals("전화번호가 일치해야 합니다.", savedMember.getTel(), signUpRequest.getTel()),
                () -> assertEquals("기본 역할이 MANAGER 여야 합니다.", savedMember.getRole().name(), MemberRole.MANAGER.name()),
                () -> assertEquals("기본 상태가 PENDING 이어야 합니다.", savedMember.getStatus().name(), MemberStatus.PENDING.name())
        );


        // memberRepository.save()가 정확히 한 번 호출되었는지 확인
        verify(memberCommandRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("중복 아이디 검사 테스트")
    void testSignUpWithDuplicateUsername() {
        // given
        when(memberQueryService.existsByUsername(signUpRequest.getUsername())).thenReturn(true);  // 아이디가 이미 존재한다고 가정

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberService.signUp(signUpRequest)
        );
        // exception 메시지가 "이미 사용 중인 아이디입니다."인지 확인
        Assertions.assertEquals(DUPLICATE_USERNAME_ERROR, exception.getMessage());

        verify(memberCommandRepository, times(0)).save(any(Member.class));
    }

    @Test
    @DisplayName("중복 전화번호 검사 테스트")
    void testSignUpWithDuplicateTel() {
        // given
        when(memberQueryService.existsByTel(signUpRequest.getTel())).thenReturn(true);  // 아이디가 이미 존재한다고 가정

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberService.signUp(signUpRequest)
        );

        // exception 메시지가 "이미 사용 중인 아이디입니다."인지 확인
        Assertions.assertEquals(DUPLICATE_TEL_ERROR, exception.getMessage());

        verify(memberCommandRepository, times(0)).save(any(Member.class));
    }


}
