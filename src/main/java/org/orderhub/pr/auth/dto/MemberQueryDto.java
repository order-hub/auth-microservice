package org.orderhub.pr.auth.dto;

import lombok.Builder;
import lombok.Data;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;

import java.util.List;
import java.util.UUID;

public class MemberQueryDto {

    @Builder
    @Data
    public static class FindMemberResponse {
        private List<FindMemberByIdResponse> members;
    }

    @Builder
    @Data
    public static class FindMemberByIdResponse{
        private UUID id;
        private String username;
        private String realName;
        private String tel;
        private MemberRole role;
        private MemberStatus status;
        boolean success;
    }

    @Builder
    @Data
    public static class FindMemberByUsernameResponse{
        private UUID id;
        private String username;
        private String realName;
        private String tel;
        private MemberRole role;
        private MemberStatus status;
        boolean success;
    }

}
