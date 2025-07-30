package com.tanishk.project.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.tanishk.project.model.AppRole;
import com.tanishk.project.model.Role;
import com.tanishk.project.model.User;
import com.tanishk.project.repo.RoleRepository;
import com.tanishk.project.repo.UserRepository;
import com.tanishk.project.security.firebase.FirebaseUtils;
import com.tanishk.project.security.jwt.JwtUtils;
import com.tanishk.project.security.request.LoginRequest;
import com.tanishk.project.security.request.SignupRequest;
import com.tanishk.project.security.response.MessageResponse;
import com.tanishk.project.security.response.UserInfoResponse;
import com.tanishk.project.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private FirebaseUtils firebaseUtils;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	RoleRepository roleRepository;
	
	//Signin 
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
		Authentication authentication;
		
		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (AuthenticationException exception) {
			exception.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("message", "Bad Credentials");
			map.put("status", false);
			return new ResponseEntity<Object>(map,HttpStatus.NOT_FOUND);
		}
		System.out.println("Authentication successful");
		SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),roles,jwtCookie.toString());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(response);
	}
	
	//Signup
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
	        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
	            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
	        }

	        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	        }

	        // Create new user's account
	        User user = new User(signUpRequest.getUsername(),
	                signUpRequest.getEmail(),
	                encoder.encode(signUpRequest.getPassword()));

	        Set<String> strRoles = signUpRequest.getRole();
	        Set<Role> roles = new HashSet<>();

	        if (strRoles == null) {
	            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
	                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	            roles.add(userRole);
	        } else {
	            strRoles.forEach(role -> {
	                switch (role) {
	                    case "admin":
	                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
	                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	                        roles.add(adminRole);

	                        break;
	                    case "seller":
	                        Role modRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
	                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	                        roles.add(modRole);

	                        break;
	                    default:
	                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
	                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	                        roles.add(userRole);
	                }
	            });
	        }

	        user.setRoles(roles);
	        userRepository.save(user);

	        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@GetMapping("/username")
	public String currentUserName(Authentication authentication) {
		if(authentication!=null) {
			return authentication.getName();
		} else {
			return "";
		}
	}
	
	@GetMapping("/user")
	public ResponseEntity<?> getUserDetails(Authentication authentication) {
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),roles);

        return ResponseEntity.ok(response);
        
	}
	
	@PostMapping("/signout")
	public ResponseEntity<?> signoutUser(){
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out"));

	}
	
//	Firebase login endpoint that accepts ID from frontend -> verifies -> checks if user exists -> if not, creates user -> generates jwt cookie
	@PostMapping("/firebase-login")
	public ResponseEntity<?> firebaseLogin(@RequestBody Map<String, String> body) {
	    String idToken = body.get("idToken");

	    try {
	        FirebaseToken firebaseToken = firebaseUtils.verifyIdToken(idToken);

	        String email = firebaseToken.getEmail();
	        String name = firebaseToken.getName(); 
	        String uid = firebaseToken.getUid();   

	        if (email == null || email.isEmpty()) {
	            return ResponseEntity.badRequest().body(Map.of("message", "Email not found in Firebase token"));
	        }
	        	        
	        // Check if user exists
	        User user = userRepository.findByEmail(email).orElse(null);

	        if (user == null) {
	            // Create user with auto-generated username
	            String username = email.split("@")[0] + "_" + uid.substring(0, 5);
	            user = new User(username, email, ""); // blank password

	            user.setRoles(Set.of(
	                roleRepository.findByRoleName(AppRole.ROLE_USER)
	                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."))
	            ));

	            userRepository.save(user);
	        }

	        // Manually build UserDetails
	        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

	        // Create JWT cookie
	        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

	        // Prepare frontend response
	        List<String> roles = userDetails.getAuthorities().stream()
	                .map(item -> item.getAuthority())
	                .collect(Collectors.toList());

	        UserInfoResponse response = new UserInfoResponse(
	                user.getUserId(),
	                user.getUserName(),
	                roles,
	                jwtCookie.toString()
	        );

	        return ResponseEntity.ok()
	                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	                .body(response);

	    } catch (FirebaseAuthException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("message", "Invalid Firebase token", "error", e.getMessage()));
	    }
	}

	
	

	
}
