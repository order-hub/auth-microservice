package org.orderhub.pr.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class Accounts {
    public static class Validation {
        public static final String USERNAME = "^[a-z]+[a-z0-9]{3,30}$";
        public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,100}$";
        public static final String PHONE_NUMBER = "^\\d{3}-\\d{3,4}-\\d{4}$";
    }
}