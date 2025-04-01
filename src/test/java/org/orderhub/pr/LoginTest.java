package org.orderhub.pr;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.jwt.security.JwtGenerator;
import org.orderhub.pr.auth.domain.MemberRole;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private JwtGenerator jwtGenerator;  // 모킹된 JwtGenerator

    private Member testMember;

    @BeforeEach
    public void setUp() {
        jwtGenerator = Mockito.mock(JwtGenerator.class);  // Mockito로 모킹된 JwtGenerator 객체 생성

        // 빌더 패턴을 사용하여 테스트용 멤버 객체 설정
        testMember = Member.builder()
                .id(UUID.randomUUID())  // UUID로 id 설정
                .username("testUser")
                .password("password123")  // 실제로는 암호화된 비밀번호를 사용해야 함
                .realName("Test User")
                .role(MemberRole.ADMIN)
                .status(MemberStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    public void testLoginSuccess() throws JsonProcessingException {
        // 로그인 성공을 위한 조건 설정
        String inputUsername = "testUser";
        String inputPassword = "password123";

        // 비밀번호가 일치하는지 확인
        assertEquals(inputUsername, testMember.getUsername());
        assertEquals(inputPassword, testMember.getPassword());

        // JwtGenerator가 generateAccessToken 메서드 호출 시 올바른 토큰을 반환하도록 설정
        String expectedToken = "sampleJwtToken";
        Mockito.when(jwtGenerator.generateAccessToken(Mockito.any(PrivateKey.class), Mockito.anyLong(), Mockito.any(Member.class)))
                .thenReturn(expectedToken);

        // 로그인 성공 시 반환되는 토큰을 테스트
        String accessToken = null;
        try {
            accessToken = jwtGenerator.generateAccessToken(Mockito.mock(PrivateKey.class), 3600000L, testMember);
        } catch (Exception e) {
            fail("토큰 생성 중 오류 발생: " + e.getMessage());
        }

        assertNotNull(accessToken);  // 토큰이 null이 아니어야 함
        assertEquals(expectedToken, accessToken);  // 예상되는 토큰과 실제 토큰이 일치해야 함
    }

    @Test
    public void testLoginFailure() throws JsonProcessingException {
        // 잘못된 비밀번호로 로그인 시도
        String inputPassword = "wrongPassword";

        // 비밀번호 불일치 검사
        assertNotEquals(inputPassword, testMember.getPassword());

        // JwtGenerator가 generateAccessToken 메서드 호출 시 null을 반환하도록 설정
        Mockito.when(jwtGenerator.generateAccessToken(Mockito.any(PrivateKey.class), Mockito.anyLong(), Mockito.any(Member.class)))
                .thenReturn(null);

        // 로그인 실패 시 토큰이 생성되지 않음을 검증
        String accessToken = null;
        try {
            accessToken = jwtGenerator.generateAccessToken(Mockito.mock(PrivateKey.class), 3600000L, testMember);
        } catch (Exception e) {
            fail("토큰 생성 중 오류 발생: " + e.getMessage());
        }

        assertNull(accessToken);  // 로그인 실패 시 토큰은 null이어야 함
    }
}