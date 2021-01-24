package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Role;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.security.jwt.JwtUtils;
import ru.vsu.csf.sqlportal.service.UserService;
import ru.vsu.csf.sqlportal.service.impl.UserDetailsImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
        String role = userDetails.getAuthorities().stream().findFirst().toString();

        User user = userService.findById(userDetails.getId());

        return ResponseEntity.ok(new LoginResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                role));
    }


    @PostMapping("/signup")
//    @PreAuthorize("hasRole('ADMIN')")
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
                Role.STUDENT);

        userService.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
