package com.tanishk.project.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import com.tanishk.project.model.AppRole;
import com.tanishk.project.model.Role;
import com.tanishk.project.model.User;
import com.tanishk.project.repo.RoleRepository;
import com.tanishk.project.repo.UserRepository;

@Service
public class FirebaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User verifyTokenAndGetUser(String idToken) throws FirebaseAuthException {
        // Step 1: Verify the token
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("No email found in Firebase token");

        }

        // Step 2: Check if user exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get(); // User already exists, return
        }

        // Step 3: Else, create new User with default ROLE_USER
        User newUser = new User();
        newUser.setUserName(name != null ? name : email.split("@")[0]);
        newUser.setEmail(email);
        newUser.setPassword(null); // Firebase user doesn't need password

        // Add default ROLE_USER
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        newUser.setRoles(Collections.singleton(userRole));

        return userRepository.save(newUser);
    }
}
