package com.bookexchange.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentUser {
    private Long id;
    private String email;
    private String role;
}
