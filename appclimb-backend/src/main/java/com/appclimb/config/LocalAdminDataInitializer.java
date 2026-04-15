package com.appclimb.config;

import com.appclimb.domain.User;
import com.appclimb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalAdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-email:admin@appclimb.local}")
    private String adminEmail;

    @Value("${app.bootstrap.admin-password:admin1234}")
    private String adminPassword;

    @Value("${app.bootstrap.admin-nickname:admin}")
    private String adminNickname;

    @Override
    public void run(String... args) {
        userRepository.findByEmail(adminEmail).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .nickname(adminNickname)
                        .role(User.Role.ADMIN)
                        .build())
        );
    }
}
