package org.orderhub.pr.auth.service;

import org.orderhub.pr.auth.domain.Member;
import org.orderhub.common.MemberRole;
import org.orderhub.common.MemberStatus;
import org.orderhub.pr.auth.dto.MemberQueryDto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MemberQueryService {
    Page<FindMemberByIdResponse> findAllMembers(Pageable pageable);
    Member findMemberEntityById(UUID id);
    FindMemberByIdResponse findMemberById(UUID id);
    FindMemberByUsernameResponse findByUsername(String username);
    Page<FindMemberByIdResponse> findByStatus(MemberStatus status, Pageable pageable);
    Page<FindMemberByIdResponse> findByRole(MemberRole role, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByTel(String tel);
}