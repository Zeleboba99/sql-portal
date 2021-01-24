package ru.vsu.csf.sqlportal.service;

import ru.vsu.csf.sqlportal.model.User;

public interface UserService {
    Boolean existsByLogin(String login);
    User save(User user);
    User findById(Long user_id);
}
