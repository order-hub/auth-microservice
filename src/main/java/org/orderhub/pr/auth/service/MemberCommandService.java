package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;

import java.util.UUID;

public interface MemberCommandService {

    SignUpResponse signUp(SignUpRequest request);

    UpdateMemberStatusResponse updateMemberStatus(UUID id, UpdateMemberStatusRequest request);

    UpdateMemberRoleResponse updateMemberRole(UUID id, UpdateMemberRoleRequest request);

    UpdatePasswordResponse updatePassword(UUID id, UpdatePasswordRequest request);

    DeleteMemberResponse deleteMember(UUID id);
}
