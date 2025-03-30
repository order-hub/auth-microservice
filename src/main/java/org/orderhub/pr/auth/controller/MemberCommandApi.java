package org.orderhub.pr.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.service.MemberCommandService;
import org.orderhub.pr.jwt.dto.*;
import org.orderhub.pr.jwt.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;

import java.util.UUID;

import static org.orderhub.pr.jwt.ResponseCode.SUCCESS;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberCommandApi {
    private final MemberCommandService memberCommandService;
    private final AuthService authService;

    // 프론트 측 JWT 발급 요청 대비용 API
    @PostMapping
    public ResponseEntity<SingleResponse<AuthResponse>> generateToken(HttpServletResponse response,
                                                                      @RequestBody TokenRequest tokenRequest) throws JsonProcessingException {
        AuthResponse authResponse = authService.generateTokens(tokenRequest, response);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), authResponse)
        );
    }

    @PostMapping("/signup")
    public SignUpResponse signUp(@RequestBody SignUpRequest signUpRequest) {

        return memberCommandService.signUp(signUpRequest);
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(HttpServletResponse response, @RequestBody LoginRequest request) throws JsonProcessingException {
        AuthResponse authResponse = authService.login(response, request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, @RequestBody LogoutRequest requestMember) {
        authService.logout(response, requestMember);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/members/role/{id}")
    public UpdateMemberRoleResponse updateUserRole(@PathVariable UUID id,
                                                   @RequestBody UpdateMemberRoleRequest request) {
        return  memberCommandService.updateMemberRole(id, request);
    }

    @PutMapping("/members/status/{id}")
    public UpdateMemberStatusResponse updateUserStatus(@PathVariable UUID id, @RequestBody UpdateMemberStatusRequest request) {
        return  memberCommandService.updateMemberStatus(id, request);
    }

    @PutMapping("/members/password/{id}")
    public UpdatePasswordResponse updatePassword(@PathVariable UUID id,
                                                               @RequestBody UpdatePasswordRequest request) {
        return memberCommandService.updatePassword(id, request);
    }

    @DeleteMapping("/members/{id}")
    public DeleteMemberResponse deleteUser(@PathVariable UUID id) {
        return memberCommandService.deleteMember(id);
    }
}
