package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberCommandDto.*;
import org.orderhub.pr.auth.dto.MemberQueryDto.*;

import java.util.UUID;

public interface MemberService {
    SignUpResponse signUp(SignUpRequest request);

    FindMemberResponse getAllMembers();
    FindMemberByIdResponse getMemberById(UUID id);

    FindMemberByUsernameResponse findByUsername(String username);

    ChangeStatusResponse changeMemberStatus(UUID id, MemberStatus status);
    UpdateMemberRoleResponse updateMemberRole(UUID id, MemberRole newRole);
    UpdatePasswordResponse updatePassword(UUID id, String newPassword);

    DeleteMemberResponse deleteMember(UUID id);
}
