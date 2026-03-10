package com.hcteol.jwt.backend.config;

import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.repositories.CompanyRepository;
import com.hcteol.jwt.backend.repositories.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Ensure at least one company exists
        if (companyRepository.count() == 0) {
            Company c = new Company();
            c.setCompanyId("INOV");
            c.setCompanyName("Innovacity Consulting");
            c.setActive(Boolean.TRUE);
            c.setShowCompany(Boolean.FALSE);
            c.setLanguage("EN");
            companyRepository.save(c);
            System.out.println("[DataInitializer] Created default company INOV");
        }

        // Ensure at least one app user exists
        if (userRepository.count() == 0) {
            // Creating builder account. It is used to prepare thesystem
            User u = User.builder()
                    .firstName("How Cher")
                    .lastName("Teo")
                    .login("sukasuka@gmail.com")
                    .password(passwordEncoder.encode(CharBuffer.wrap("password")))
                    .active(1)
                    .level(9)
                    .companyId("INOV")
                    .build();
            userRepository.save(u);
            System.out.println("[DataInitializer] Created default user sukasuka@gmail.com");
        }
    }
}
