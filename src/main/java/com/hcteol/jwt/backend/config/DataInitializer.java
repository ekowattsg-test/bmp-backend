package com.hcteol.jwt.backend.config;

import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.Role;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import java.time.LocalDate;
import com.hcteol.jwt.backend.entities.StockMovementCode;
import com.hcteol.jwt.backend.entities.Language;
import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.entities.UserRole;
import com.hcteol.jwt.backend.repositories.CompanyRepository;
import com.hcteol.jwt.backend.repositories.RoleRepository;
import com.hcteol.jwt.backend.repositories.StockMovementCodeRepository;
import com.hcteol.jwt.backend.repositories.LanguageRepository;
import com.hcteol.jwt.backend.repositories.UserRepository;
import com.hcteol.jwt.backend.repositories.UserRoleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Optional;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Component
@ConditionalOnProperty(prefix = "app.data.init", name = "enabled", havingValue = "true", matchIfMissing = true)
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

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private StockMovementCodeRepository stockMovementCodeRepository;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Ensure at least one company exists. Load initial company data from initData/company.json if available
        if (companyRepository.count() == 0) {
            String companyId = "INOV";
            String companyName = "Innovacity Consulting";
            String language = "EN";
            boolean active = true;
            boolean showCompany = false;

            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream is = null;
                ClassPathResource cpr = new ClassPathResource("initData/company.json");
                if (cpr.exists()) {
                    is = cpr.getInputStream();
                } else {
                    File f = new File("initData/company.json");
                    if (f.exists()) {
                        is = new java.io.FileInputStream(f);
                    }
                }

                if (is != null) {
                    Map<?,?> m = mapper.readValue(is, Map.class);
                    if (m.get("company_id") != null) companyId = String.valueOf(m.get("company_id"));
                    else if (m.get("companyId") != null) companyId = String.valueOf(m.get("companyId"));

                    if (m.get("company_name") != null) companyName = String.valueOf(m.get("company_name"));
                    else if (m.get("companyName") != null) companyName = String.valueOf(m.get("companyName"));

                    if (m.get("language") != null) language = String.valueOf(m.get("language"));

                    if (m.get("active") != null) {
                        try { active = Integer.parseInt(String.valueOf(m.get("active"))) != 0; } catch (Exception ex) {}
                    }

                    if (m.get("show_company") != null) {
                        try { showCompany = Integer.parseInt(String.valueOf(m.get("show_company"))) != 0; } catch (Exception ex) {}
                    } else if (m.get("showCompany") != null) {
                        try { showCompany = Boolean.parseBoolean(String.valueOf(m.get("showCompany"))); } catch (Exception ex) {}
                    }
                } else {
                    System.out.println("[DataInitializer] initData/company.json not found; using defaults");
                }
            } catch (Exception ex) {
                System.out.println("[DataInitializer] Failed to read initData/company.json: " + ex.getMessage());
            }

            Company c = new Company();
            c.setCompanyId(companyId);
            c.setCompanyName(companyName);
            c.setActive(active);
            c.setShowCompany(showCompany);
            c.setLanguage(language);
            companyRepository.save(c);
            System.out.println("[DataInitializer] Created default company " + companyId);
        }

        // Ensure at least one app user exists
        String builderLoginDefault = "sukasuka@gmail.com";
        String builderLogin = builderLoginDefault;
        if (userRepository.count() == 0) {
            // Load builder account content from initData/builder.json if available
            String firstName = "How Cher";
            String lastName = "Teo";
            String password = "password";
            int active = 1;
            int level = 9;
            String companyId = "INOV";

            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream is = null;
                ClassPathResource cpr = new ClassPathResource("initData/builder.json");
                if (cpr.exists()) {
                    is = cpr.getInputStream();
                } else {
                    File f = new File("initData/builder.json");
                    if (f.exists()) {
                        is = new java.io.FileInputStream(f);
                    }
                }

                if (is != null) {
                    Map<?,?> m = mapper.readValue(is, Map.class);
                    // tolerate a few key name variants (typos, snake_case, camelCase)
                    if (m.get("first_name") != null) firstName = String.valueOf(m.get("first_name"));
                    else if (m.get("irst_name") != null) firstName = String.valueOf(m.get("irst_name"));
                    else if (m.get("firstName") != null) firstName = String.valueOf(m.get("firstName"));

                    if (m.get("last_name") != null) lastName = String.valueOf(m.get("last_name"));
                    else if (m.get("lastName") != null) lastName = String.valueOf(m.get("lastName"));

                    if (m.get("login") != null) builderLogin = String.valueOf(m.get("login"));
                    else if (m.get("email") != null) builderLogin = String.valueOf(m.get("email"));

                    if (m.get("password") != null) password = String.valueOf(m.get("password"));

                    if (m.get("active") != null) {
                        try { active = Integer.parseInt(String.valueOf(m.get("active"))); } catch (Exception ex) {}
                    }

                    if (m.get("level") != null) {
                        try { level = Integer.parseInt(String.valueOf(m.get("level"))); } catch (Exception ex) {}
                    }

                    if (m.get("company_id") != null) companyId = String.valueOf(m.get("company_id"));
                    else if (m.get("companyId") != null) companyId = String.valueOf(m.get("companyId"));
                } else {
                    System.out.println("[DataInitializer] initData/builder.json not found; using defaults");
                }
            } catch (Exception ex) {
                System.out.println("[DataInitializer] Failed to read initData/builder.json: " + ex.getMessage());
            }

            // Creating builder account. It is used to prepare the system
                User u = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .login(builderLogin)
                    .password(passwordEncoder.encode(CharBuffer.wrap(password)))
                    .active(active)
                    .level(level)
                    .companyId(companyId)
                    .build();
                u.setLastPasswordChanged(new Date());
                userRepository.save(u);
            System.out.println("[DataInitializer] Created default user " + builderLogin);
        }

        // Read roles from initData/role.json, create any missing roles and assign each to the builder user
        List<Map<String,Object>> roleDefs = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/role.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/role.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    List<?> raw = mapper.readValue(content, List.class);
                    for (Object o : raw) {
                        if (o instanceof Map) roleDefs.add((Map<String,Object>)o);
                    }
                } catch (Exception ex) {
                    // fallback: extract JSON objects by brace matching
                    int len = content.length();
                    int i = 0;
                    while (i < len) {
                        if (content.charAt(i) == '{') {
                            int depth = 0;
                            int start = i;
                            while (i < len) {
                                char c = content.charAt(i);
                                if (c == '{') depth++;
                                else if (c == '}') depth--;
                                i++;
                                if (depth == 0) break;
                            }
                            if (depth == 0) {
                                String objStr = content.substring(start, i);
                                try {
                                    Map<String,Object> m = mapper.readValue(objStr, Map.class);
                                    roleDefs.add(m);
                                } catch (Exception ex2) {
                                    // ignore malformed object
                                }
                            }
                        } else {
                            i++;
                        }
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/role.json not found; skipping role import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/role.json: " + ex.getMessage());
        }

        // Ensure roles from file exist and assign them to builder
        Optional<User> builderOpt = userRepository.findByLogin(builderLogin);
        User builder = null;
        if (builderOpt.isPresent()) builder = builderOpt.get();

        List<Role> existingRoles = roleRepository.findAll();
        for (Map<String,Object> rd : roleDefs) {
            String roleName = rd.getOrDefault("role", rd.get("role_name")) != null ? String.valueOf(rd.getOrDefault("role", rd.get("role_name"))) : null;
            if (roleName == null) continue;

            String description = rd.get("description") != null ? String.valueOf(rd.get("description")) : null;
            int level = 1;
            try { if (rd.get("level") != null) level = Integer.parseInt(String.valueOf(rd.get("level"))); } catch (Exception ex) {}
            String menu = rd.get("menu") != null ? String.valueOf(rd.get("menu")) : null;

            Role role = existingRoles.stream().filter(r -> roleName.equals(r.getRole())).findFirst().orElse(null);
            if (role == null) {
                role = new Role();
                role.setRole(roleName);
                if (description != null) role.setDescription(description);
                role.setLevel(level);
                if (menu != null) role.setMenu(menu);
                roleRepository.save(role);
                System.out.println("[DataInitializer] Created role " + roleName);
                existingRoles = roleRepository.findAll(); // refresh
            }

            // assign to builder if present
            if (builder != null) {
                UserRole existing = userRoleRepository.findByUserIdAndRoleId(builder.getId(), role.getId());
                if (existing == null) {
                    UserRole ur = new UserRole();
                    ur.setUserId(builder.getId());
                    ur.setRoleId(role.getId());
                    userRoleRepository.save(ur);
                    System.out.println("[DataInitializer] Assigned role " + roleName + " to user " + builder.getLogin());
                }
            }
        }

        // Recreate userrole_view: drop if exists then create
        // Load languages from initData/language.json and ensure they exist in DB
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/language.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/language.json");
                if (f.exists()) is = new java.io.FileInputStream(f);
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) continue;
                    java.util.Map m = (java.util.Map)o;
                    Object codeObj = m.get("code");
                    Object nameObj = m.get("name");
                    if (codeObj == null) continue;
                    String code = String.valueOf(codeObj);
                    String name = nameObj != null ? String.valueOf(nameObj) : null;
                    if (!languageRepository.existsById(code)) {
                        Language lang = new Language();
                        lang.setCode(code);
                        if (name != null) lang.setName(name);
                        languageRepository.save(lang);
                        System.out.println("[DataInitializer] Created language " + code + (name != null ? " ("+name+")" : ""));
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/language.json not found; skipping language import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/language.json: " + ex.getMessage());
        }
        
        // Load stock movement codes from initData/movement.json: insert or update by movement_type
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/movement.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/movement.json");
                if (f.exists()) is = new java.io.FileInputStream(f);
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) continue;
                    java.util.Map m = (java.util.Map)o;
                    Object mtObj = m.get("movement_type");
                    if (mtObj == null) mtObj = m.get("movementType");
                    if (mtObj == null) mtObj = m.get("movement");
                    if (mtObj == null) continue;
                    String movementType = String.valueOf(mtObj).trim();
                    if (movementType.length() == 0) continue;
                    if (movementType.length() > 1) movementType = movementType.substring(0,1);

                    String movementDescription = m.get("movement_description") != null ? String.valueOf(m.get("movement_description")) : (m.get("movementDescription") != null ? String.valueOf(m.get("movementDescription")) : null);
                    Integer stockModifier = 0;
                    Integer holdModifier = 0;
                    try { if (m.get("stock_modifier") != null) stockModifier = Integer.parseInt(String.valueOf(m.get("stock_modifier"))); else if (m.get("stockModifier") != null) stockModifier = Integer.parseInt(String.valueOf(m.get("stockModifier"))); } catch (Exception ex) {}
                    try { if (m.get("hold_modifier") != null) holdModifier = Integer.parseInt(String.valueOf(m.get("hold_modifier"))); else if (m.get("holdModifier") != null) holdModifier = Integer.parseInt(String.valueOf(m.get("holdModifier"))); } catch (Exception ex) {}

                    java.util.Optional<StockMovementCode> existingOpt = stockMovementCodeRepository.findById(movementType);
                    if (existingOpt.isPresent()) {
                        StockMovementCode sm = existingOpt.get();
                        if (movementDescription != null) sm.setMovementDescription(movementDescription);
                        sm.setStockModifier(stockModifier);
                        sm.setHoldModifier(holdModifier);
                        stockMovementCodeRepository.save(sm);
                        System.out.println("[DataInitializer] Updated stock movement code " + movementType);
                    } else {
                        StockMovementCode sm = new StockMovementCode();
                        sm.setMovementType(movementType);
                        if (movementDescription != null) sm.setMovementDescription(movementDescription);
                        sm.setStockModifier(stockModifier);
                        sm.setHoldModifier(holdModifier);
                        stockMovementCodeRepository.save(sm);
                        System.out.println("[DataInitializer] Created stock movement code " + movementType);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/movement.json not found; skipping stock movement import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/movement.json: " + ex.getMessage());
        }
        // Ensure baselineDate param exists; default to first day of current year at midnight
        if (!paramRepository.existsById("baselineDate")) {
            LocalDate firstDayOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            String baselineDateValue = firstDayOfYear + "T00:00:00";
            Param baselineDate = new Param();
            baselineDate.setParam_key("baselineDate");
            baselineDate.setValue_string(baselineDateValue);
            paramRepository.save(baselineDate);
            System.out.println("[DataInitializer] Created param 'baselineDate' with value " + baselineDateValue);
        }

        try {
            // Keep view DDL in resources so DB schema SQL is not hardcoded in Java.
            // Execute all SQL files under sqlView in lexical order.
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] viewScripts = resolver.getResources("classpath*:sqlView/*.sql");
            Arrays.sort(viewScripts, Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareToIgnoreCase)));

            try (Connection connection = dataSource.getConnection()) {
                for (Resource viewScript : viewScripts) {
                    ScriptUtils.executeSqlScript(connection, viewScript);
                    System.out.println("[DataInitializer] Executed view script " + viewScript.getFilename());
                }
            }

            if (viewScripts.length == 0) {
                System.out.println("[DataInitializer] No SQL view scripts found in sqlView/");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to execute SQL view scripts: " + ex.getMessage());
        }
    }
}
