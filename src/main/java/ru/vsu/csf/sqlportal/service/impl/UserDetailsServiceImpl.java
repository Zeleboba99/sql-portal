package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(s)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + s));

        return UserDetailsImpl.build(user);
    }
}
