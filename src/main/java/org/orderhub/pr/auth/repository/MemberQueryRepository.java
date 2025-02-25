package org.orderhub.pr.auth.repository;

import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberQueryRepository extends JpaRepository<Member, UUID> {
    boolean existsByUsername(String username);
    boolean existsByTel(String tel);

    Optional<Member> findByUsername(String username);
    List<Member> findByStatus(MemberStatus status);
    List<Member> findByRole(MemberRole role);
}
