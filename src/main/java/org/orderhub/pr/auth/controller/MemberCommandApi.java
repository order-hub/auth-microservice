package org.orderhub.pr.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.service.MemberCommandService;
import org.orderhub.pr.config.resolver.CurrentMemberId;
import org.orderhub.pr.jwt.dto.*;
import org.orderhub.pr.jwt.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberCommandApi {
    private final MemberCommandService memberCommandService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SimpleSuccessResponse>> signUp(@RequestBody SignUpRequest signUpRequest) {

        return ResponseEntity.ok(memberCommandService.signUp(signUpRequest));
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(HttpServletResponse response, @RequestBody LoginRequest request) throws JsonProcessingException {
        AuthResponse authResponse = authService.login(response, request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, @RequestBody LogoutRequest requestMember) {
        authService.logout(request, response, requestMember);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/members/role")
    public ResponseEntity<ApiResponse<SimpleSuccessResponse>> updateUserRole(@CurrentMemberId UUID id,
                                                   @RequestBody UpdateMemberRoleRequest request) {
        return ResponseEntity.ok(memberCommandService.updateMemberRole(id, request));
    }

    @PutMapping("/members/status")
    public ResponseEntity<ApiResponse<SimpleSuccessResponse>> updateUserStatus(@CurrentMemberId UUID id, @RequestBody UpdateMemberStatusRequest request) {
        return ResponseEntity.ok(memberCommandService.updateMemberStatus(id, request));
    }

    @PutMapping("/members/password")
    public ResponseEntity<ApiResponse<SimpleSuccessResponse>> updatePassword(@CurrentMemberId UUID id,
                                                               @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(memberCommandService.updatePassword(id, request));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<ApiResponse<SimpleSuccessResponse>> deleteUser(@PathVariable UUID id) {
        return ResponseEntity.ok(memberCommandService.deleteMember(id));
    }
}
