package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;

import java.util.UUID;

public interface MemberCommandService {

    SignUpResponse signUp(SignUpRequest request);

    UpdateStatusResponse updateMemberStatus(UUID id, MemberStatus status);

    UpdateMemberRoleResponse updateMemberRole(UUID id, MemberRole newRole);

    UpdatePasswordResponse updatePassword(UUID id, UpdatePasswordRequest request);

    DeleteMemberResponse deleteMember(UUID id);
}
