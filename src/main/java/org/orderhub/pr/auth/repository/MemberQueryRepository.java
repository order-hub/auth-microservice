package org.orderhub.pr.auth.repository;

import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberQueryRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUsername(String username);

    // TODO: 추후에 해당 메서드 사용 예정
    List<Member> findByStatus(MemberStatus status);
}
