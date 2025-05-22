package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.dto.MemberCommandDto.*;

import java.util.UUID;

public interface MemberCommandService {

    ApiResponse<SimpleSuccessResponse> signUp(SignUpRequest request);

    ApiResponse<SimpleSuccessResponse> updateMemberStatus(UUID id, UpdateMemberStatusRequest request);

    ApiResponse<SimpleSuccessResponse> updateMemberRole(UUID id, UpdateMemberRoleRequest request);

    ApiResponse<SimpleSuccessResponse> updatePassword(UUID id, UpdatePasswordRequest request);

    ApiResponse<SimpleSuccessResponse> deleteMember(UUID id);
}
