package com.bookexchange.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtil {

    public static CurrentUser get() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CurrentUser user) {
            return user;
        }
        return null;
    }
}
