package com.hcteol.jwt.backend.config;

import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.entities.Role;
import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.entities.UserRole;
import com.hcteol.jwt.backend.repositories.CompanyRepository;
import com.hcteol.jwt.backend.repositories.RoleRepository;
import com.hcteol.jwt.backend.repositories.UserRepository;
import com.hcteol.jwt.backend.repositories.UserRoleRepository;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

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
            // Creating builder account. It is used to prepare the system
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

        // Ensure at least two roles exist; if not, create BaseSetup and Admin
        if (roleRepository.count() < 2) {
            Role base = new Role();
            base.setDescription("System Base Set Up");
            base.setRole("BaseSetup");
            base.setLevel(9);
            base.setMenu("BaseSetup");
            roleRepository.save(base);

            Role admin = new Role();
            admin.setDescription("Administrator");
            admin.setRole("Admin");
            admin.setLevel(9);
            admin.setMenu("Admin");
            roleRepository.save(admin);

            System.out.println("[DataInitializer] Created default roles BaseSetup and Admin");
        }

        // Assign both roles to the builder account (sukasuka@gmail.com)
        Optional<User> builderOpt = userRepository.findByLogin("sukasuka@gmail.com");
        if (builderOpt.isPresent()) {
            User builder = builderOpt.get();
            List<Role> roles = roleRepository.findAll();
            for (String roleName : new String[] {"BaseSetup", "Admin"}) {
                Role roleToAssign = roles.stream().filter(r -> roleName.equals(r.getRole())).findFirst().orElse(null);
                if (roleToAssign != null) {
                    UserRole existing = userRoleRepository.findByUserIdAndRoleId(builder.getId(), roleToAssign.getId());
                    if (existing == null) {
                        UserRole ur = new UserRole();
                        ur.setUserId(builder.getId());
                        ur.setRoleId(roleToAssign.getId());
                        userRoleRepository.save(ur);
                        System.out.println("[DataInitializer] Assigned role " + roleName + " to user sukasuka@gmail.com");
                    }
                }
            }
        } else {
            System.out.println("[DataInitializer] Builder user sukasuka@gmail.com not found; skipping role assignment");
        }
    }
}
