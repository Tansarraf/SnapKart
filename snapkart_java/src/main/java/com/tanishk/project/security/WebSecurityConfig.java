package com.tanishk.project.security;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tanishk.project.model.AppRole;
import com.tanishk.project.model.Role;
import com.tanishk.project.model.User;
import com.tanishk.project.repo.RoleRepository;
import com.tanishk.project.repo.UserRepository;
import com.tanishk.project.security.jwt.AuthEntryPointJwt;
import com.tanishk.project.security.jwt.AuthTokenFilter;
import com.tanishk.project.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
        		.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                //.requestMatchers("/api/admin/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }
	
	 @Bean
	    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
	        return args -> {
	            // Retrieve or create roles
	            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
	                    .orElseGet(() -> {
	                        Role newUserRole = new Role(AppRole.ROLE_USER);
	                        return roleRepository.save(newUserRole);
	                    });

	            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
	                    .orElseGet(() -> {
	                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
	                        return roleRepository.save(newSellerRole);
	                    });

	            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
	                    .orElseGet(() -> {
	                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
	                        return roleRepository.save(newAdminRole);
	                    });

	            Set<Role> userRoles = Set.of(userRole);
	            Set<Role> sellerRoles = Set.of(sellerRole);
	            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


	            // Create users if not already present
	            if (!userRepository.existsByUserName("user1")) {
	                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
	                userRepository.save(user1);
	            }

	            if (!userRepository.existsByUserName("seller1")) {
	                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
	                userRepository.save(seller1);
	            }

	            if (!userRepository.existsByUserName("admin")) {
	                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
	                userRepository.save(admin);
	            }

	            // Update roles for existing users
	            userRepository.findByUserName("user1").ifPresent(user -> {
	                user.setRoles(userRoles);
	                userRepository.save(user);
	            });

	            userRepository.findByUserName("seller1").ifPresent(seller -> {
	                seller.setRoles(sellerRoles);
	                userRepository.save(seller);
	            });

	            userRepository.findByUserName("admin").ifPresent(admin -> {
	                admin.setRoles(adminRoles);
	                userRepository.save(admin);
	            });
	        };
	    }
	 @Bean
	 public CorsFilter corsFilter() {
	     CorsConfiguration config = new CorsConfiguration();
	     config.setAllowCredentials(true);
	     config.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend
	     config.setAllowedHeaders(List.of("*"));
	     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

	     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	     source.registerCorsConfiguration("/**", config);
	     return new CorsFilter(source);
	 }

}
