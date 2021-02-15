package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.sqlportal.dto.request.LoginRequest;
import ru.vsu.csf.sqlportal.dto.request.SignupRequest;
import ru.vsu.csf.sqlportal.dto.response.LoginResponse;
import ru.vsu.csf.sqlportal.dto.response.UserResponse;
import ru.vsu.csf.sqlportal.model.Role;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.security.jwt.JwtUtils;
import ru.vsu.csf.sqlportal.service.UserService;
import ru.vsu.csf.sqlportal.service.impl.UserDetailsImpl;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(loginRequest.getLogin());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream().findFirst().get().toString();

        User user = userService.findById(userDetails.getId());

        return ResponseEntity.ok(new LoginResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                role));
    }


    @PostMapping("/signup")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.existsByLogin(signUpRequest.getLogin())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        // Create new user's account

        User user = new User(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getLogin(),
                encoder.encode(signUpRequest.getPassword()),
                Role.valueOf(signUpRequest.getRole()));

        userService.save(user);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/users/{user_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse getUserById(@PathVariable("user_id") Long user_id) {
        return userService.getUserById(user_id);
    }

    @PutMapping("/users/{user_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("user_id") Long user_id,
                                        @Valid @RequestBody SignupRequest signUpRequest) {
        userService.updateUser(user_id, signUpRequest);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/users/teachers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserResponse> getAllTeachers(@RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam(value = "sort", defaultValue = "true") boolean sort) {
        return userService.getAllByRole(Role.TEACHER, page, size, sort);
    }

    @GetMapping("/users/students")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Page<UserResponse> getAllStudents(@RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam(value = "sort", defaultValue = "true") boolean sort) {
        return userService.getAllByRole(Role.STUDENT, page, size, sort);
    }

    @DeleteMapping("/users/{user_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(@PathVariable("user_id") Long user_id) {
        userService.deleteUser(user_id);
    }
}

