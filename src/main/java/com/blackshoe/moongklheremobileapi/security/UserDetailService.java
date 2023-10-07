package com.blackshoe.moongklheremobileapi.security;

import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email : " + userEmail)
        );

        return UserPrincipal.create(user);
    }
}
