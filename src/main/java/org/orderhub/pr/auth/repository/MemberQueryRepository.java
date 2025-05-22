package org.orderhub.pr.auth.repository;

import org.orderhub.pr.auth.domain.Member;
import org.orderhub.common.MemberRole;
import org.orderhub.common.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberQueryRepository extends JpaRepository<Member, UUID> {
    boolean existsByUsername(String username);
    boolean existsByTel(String tel);
    Optional<Member> findByUsername(String username);
    Page<Member> findByStatus(MemberStatus status, Pageable pageable);
    Page<Member> findByRole(MemberRole role, Pageable pageable);
}
