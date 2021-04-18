package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.dto.request.LoginRequest;
import ru.vsu.csf.sqlportal.dto.request.SignupRequest;
import ru.vsu.csf.sqlportal.dto.response.LoginResponse;
import ru.vsu.csf.sqlportal.dto.response.UserResponse;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Role;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.security.jwt.JwtUtils;
import ru.vsu.csf.sqlportal.service.ConverterService;
import ru.vsu.csf.sqlportal.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.vsu.csf.sqlportal.service.ConverterService.convertToUserResponse;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(loginRequest.getLogin());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream().findFirst().get().toString();

        User user = findById(userDetails.getId());

        return new LoginResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                role);
    }

    @Override
    public User registerUser(SignupRequest signupRequest) {
        User user = new User(
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getLogin(),
                encoder.encode(signupRequest.getPassword()),
                Role.valueOf(signupRequest.getRole()));
        return save(user);
    }

    @Override
    public void changePassword(String password) {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(userLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", userLogin)
        );
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

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
                .map(ConverterService::convertToUserResponse).collect(Collectors.toList());
        return new PageImpl<>(userResponses, PageRequest.of(page, size), totalElements);
    }

    @Override
    public UserResponse getUserById(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", user_id)
        );
        return convertToUserResponse(user);
    }

    @Override
    public Page<UserResponse> searchStudents(String search, int page, int size) {
        String searchValue = search.replaceAll("\\s+", "").toLowerCase();
        List<UserResponse> userResponses = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .filter(user -> {
                    String userFullNameV1 = user.getFirstName().concat(user.getLastName()).toLowerCase();
                    String userFullNameV2 = user.getLastName().concat(user.getFirstName()).toLowerCase();
                    return userFullNameV1.contains(searchValue) ||
                            userFullNameV2.contains(searchValue) ||
                            user.getLogin().contains(searchValue);
                })
                .map(ConverterService::convertToUserResponse)
                .collect(Collectors.toList());

        int start =  (int)PageRequest.of(page, size).getOffset();
        int end = Math.min((start + PageRequest.of(page, size).getPageSize()), userResponses.size());
        return new PageImpl<>(userResponses.subList(start, end), PageRequest.of(page, size), userResponses.size());
    }
}
