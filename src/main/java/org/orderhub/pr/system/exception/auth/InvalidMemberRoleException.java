package org.orderhub.pr.system.exception.auth;

public class InvalidMemberRoleException extends RuntimeException {
    public InvalidMemberRoleException(String message) {
        super(message);
    }
}