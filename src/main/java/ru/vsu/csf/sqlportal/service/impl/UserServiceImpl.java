package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long user_id) {
        return userRepository.findById(user_id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", user_id)
        );
    }
}
