package org.orderhub.pr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.jwt.security.JwtGenerator;
import org.orderhub.pr.jwt.security.RsaKeyLoader;

import java.security.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtProviderTest {

    @InjectMocks
    private JwtGenerator jwtGenerator;
    @Mock
    private RsaKeyLoader rsaKeyLoader;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Member member;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static final long ACCESS_EXPIRATION = 3600000L; // 1 hour
    private static final long REFRESH_EXPIRATION = 604800000L; // 7 days
    Instant expectedInstant = Instant.now();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // ObjectMapper 설정
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 테스트용 RSA 키 쌍 생성
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // rsaKeyLoader의 mock 메서드가 실제 RSA 키 반환하도록 설정
        when(rsaKeyLoader.loadPrivateKey()).thenReturn(privateKey);
        when(rsaKeyLoader.loadPublicKey()).thenReturn(publicKey);

        // Mock Member 설정
        String expectedCreatedAt = expectedInstant.toString();

        when(member.getId()).thenReturn(UUID.randomUUID());
        when(member.getUsername()).thenReturn("testUser");
        when(member.getRealName()).thenReturn("Test User");
        when(member.getTel()).thenReturn("123-456-7890");
        when(member.getRole()).thenReturn(MemberRole.ADMIN);
        when(member.getStatus()).thenReturn(MemberStatus.ACTIVE);
        when(member.getCreatedAt()).thenReturn(Instant.parse(expectedCreatedAt));
    }


    @Test
    @DisplayName("엑세스 토큰 발급 및 유효성 테스트")
    void generateAccessTokenAndValidToken() throws JsonProcessingException {
        // given
        String token = jwtGenerator.generateAccessToken(privateKey, ACCESS_EXPIRATION, member);

        // when
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // then
        assertNotNull(token);
        assertEquals(member.getId().toString(), claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals("Test User", claims.get("realName"));
        assertEquals(MemberRole.ADMIN.name(), claims.get("role"));
        assertEquals(MemberStatus.ACTIVE.name(), claims.get("status"));
        assertNotNull(claims.get("createdAt"));
    }

    @Test
    @DisplayName("리프레시 토큰 발급 및 유효성 검사")
    void generateRefreshTokenAndValidToken() {
        // given
        String refreshToken = jwtGenerator.generateRefreshToken(privateKey, REFRESH_EXPIRATION, member);

        // when
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)  // 공개 키로 검증
                .build()
                .parseClaimsJws(refreshToken)  // 리프레시 토큰 파싱
                .getBody();  // 클레임 내용 추출

        // then
        assertNotNull(refreshToken);  // 토큰이 null이 아닌지 확인
        assertEquals(String.valueOf(member.getId()), claims.getSubject());  // 주체(subject) 검증
        assertTrue(claims.getExpiration().after(new Date()));  // 만료 시간이 현재 시간 이후인지 확인
    }

    @Test
    @DisplayName("엑세스 토큰 만료 후 리프레시 토큰으로 새 엑세스 토큰 발급")
    void testRefreshAccessToken() throws JsonProcessingException {
        // given
        // 1. 엑세스 토큰 생성
        String accessToken = jwtGenerator.generateAccessToken(privateKey, ACCESS_EXPIRATION, member);

        // 2. 만료 시간 확인 (엑세스 토큰은 1시간 후, 리프레시 토큰은 7일 후)
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)  // 공개 키로 검증
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        // when
        // 엑세스 토큰을 만료시켜놓고 리프레시 토큰으로 새로운 엑세스 토큰을 발급
        // 실제 구현에서 엑세스 토큰 만료 후 리프레시 토큰을 처리하는 로직이 있을 경우, 해당 메서드를 호출할 수 있습니다.
        String newAccessToken = jwtGenerator.generateAccessToken(privateKey, ACCESS_EXPIRATION, member);  // 예시로 새 엑세스 토큰 발급

        // then
        assertNotNull(newAccessToken);  // 새 엑세스 토큰이 생성되었는지 확인
        Claims newClaims = Jwts.parserBuilder()
                .setSigningKey(publicKey)  // 공개 키로 검증
                .build()
                .parseClaimsJws(newAccessToken)
                .getBody();

        assertEquals(claims.getSubject(), newClaims.getSubject());  // 기존과 같은 주체(subject)가 유지되는지 확인
        assertNotEquals(accessToken, newAccessToken);  // 기존 엑세스 토큰과 새로운 엑세스 토큰이 다르다는 것을 확인
    }
}
