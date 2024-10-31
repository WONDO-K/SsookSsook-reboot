package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Parent에서 먼저 검색
        return parentRepository.findByLoginId(username)
                .map(CustomUserDetails::new)
                .orElseGet(() -> childRepository.findByLoginId(username)
                        .map(CustomUserDetails::new)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
