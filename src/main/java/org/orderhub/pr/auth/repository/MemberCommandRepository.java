package org.orderhub.pr.auth.repository;

import org.orderhub.pr.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberCommandRepository extends JpaRepository<Member, UUID> {
    boolean existsByUsername(String username);
    boolean existsByTel(String tel);
}
