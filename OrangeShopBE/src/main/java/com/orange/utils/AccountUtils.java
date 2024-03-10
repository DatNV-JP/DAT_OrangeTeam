package com.orange.utils;

import com.orange.domain.model.User;
import com.orange.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountUtils {
    private final IUserRepository userRepository;
    public static String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    public static boolean isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_STAFF") || r.getAuthority().equals("ROLE_ADMIN"));
    }
    public User getUser() {
        return userRepository.findByUsername(AccountUtils.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User không tồn tại!"));
    }
    public User getUserOrAnonymousUser() {
        return userRepository.findByUsername(AccountUtils.getUsername()).orElse(null);
    }
    public Long getUserId() {
        return getUser().getId();
    }
}
