package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.orderhub.pr.auth.dto.MemberQueryDto.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberQueryService {

    List<FindMemberByIdResponse> findAllMembers();

    Member findMemberEntityById(UUID id);
    Optional<FindMemberByIdResponse> findMemberById(UUID id);
    Optional<FindMemberByUsernameResponse> findByUsername(String username);

    List<FindMemberByIdResponse> findByStatus(MemberStatus status);
    List<FindMemberByIdResponse> findByRole(MemberRole role);

    boolean existsByUsername(String username);
    boolean existsByTel(String tel);

}
