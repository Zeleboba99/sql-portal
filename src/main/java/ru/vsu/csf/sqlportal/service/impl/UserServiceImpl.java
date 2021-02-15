package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.dto.request.SignupRequest;
import ru.vsu.csf.sqlportal.dto.response.UserResponse;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Role;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    PasswordEncoder encoder;

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
    public User updateUser(Long user_id, SignupRequest signupRequest) {
        User user = findById(user_id);
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setLogin(signupRequest.getLogin());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setRole(Role.valueOf(signupRequest.getRole()));
        return userRepository.save(user);
    }

    @Override
    public User findById(Long user_id) {
        return userRepository.findById(user_id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", user_id)
        );
    }

    @Override
    public void deleteUser(Long user_id) {
        userRepository.deleteById(user_id);
    }

    @Override
    public Page<UserResponse> getAllByRole(Role role, int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("lastName").ascending() : Sort.by("lastName").descending();
        Page<User> usersPage = userRepository.findAllByRole(role, PageRequest.of(page, size, sortOrder));
        long totalElements = usersPage.getTotalElements();
        List<UserResponse> userResponses = usersPage.stream()
                .map(this::convertToUserResponse).collect(Collectors.toList());
        return new PageImpl<>(userResponses, PageRequest.of(page, size), totalElements);
    }

    @Override
    public UserResponse getUserById(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", user_id)
        );
        return convertToUserResponse(user);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(user.getId(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name());
    }

}
