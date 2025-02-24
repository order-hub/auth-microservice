package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.dto.MemberQueryDto.*;

import java.util.UUID;

public interface MemberQueryService {

    // TODO: 추후에 해당 메서드 사용 예정
    FindMemberResponse getAllMembers();
    // TODO: 추후에 해당 메서드 사용 예정
    FindMemberByIdResponse getMemberById(UUID id);
    // TODO: 추후에 해당 메서드 사용 예정
    FindMemberByUsernameResponse findByUsername(String username);

}
