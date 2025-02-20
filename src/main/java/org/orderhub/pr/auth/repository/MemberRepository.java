package org.orderhub.pr.auth.repository;

import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Member> findByStatus(MemberStatus status);
}
