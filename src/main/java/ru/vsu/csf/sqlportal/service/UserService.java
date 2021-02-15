package ru.vsu.csf.sqlportal.service;

import org.springframework.data.domain.Page;
import ru.vsu.csf.sqlportal.dto.request.SignupRequest;
import ru.vsu.csf.sqlportal.dto.response.UserResponse;
import ru.vsu.csf.sqlportal.model.Role;
import ru.vsu.csf.sqlportal.model.User;

import java.util.List;

public interface UserService {
    Boolean existsByLogin(String login);
    User save(User user);
    User updateUser(Long user_id, SignupRequest signupRequest);
    User findById(Long user_id);
    void deleteUser(Long user_id);
    Page<UserResponse> getAllByRole(Role role, int page, int size, boolean sort);
    UserResponse getUserById(Long user_id);
}
