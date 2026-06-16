package com.hcteol.jwt.backend.config;

import java.io.File;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.entities.DocumentSeq;
import com.hcteol.jwt.backend.entities.Language;
import com.hcteol.jwt.backend.entities.OperationRole;
import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.ProjectTaskType;
import com.hcteol.jwt.backend.entities.Role;
import com.hcteol.jwt.backend.entities.StockMovementCode;
import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.entities.UserRole;
import com.hcteol.jwt.backend.entities.WorkOrderEntity;
import com.hcteol.jwt.backend.entities.WorkOrderType;
import com.hcteol.jwt.backend.entities.WorkStepsType;
import com.hcteol.jwt.backend.repositories.CompanyRepository;
import com.hcteol.jwt.backend.repositories.DocumentSeqRepository;
import com.hcteol.jwt.backend.repositories.LanguageRepository;
import com.hcteol.jwt.backend.repositories.OperationRoleRepository;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskTypeRepository;
import com.hcteol.jwt.backend.repositories.RoleRepository;
import com.hcteol.jwt.backend.repositories.StockMovementCodeRepository;
import com.hcteol.jwt.backend.repositories.UserRepository;
import com.hcteol.jwt.backend.repositories.UserRoleRepository;
import com.hcteol.jwt.backend.repositories.WorkOrderEntityRepository;
import com.hcteol.jwt.backend.repositories.WorkOrderTypeRepository;
import com.hcteol.jwt.backend.repositories.WorkStepsTypeRepository;

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
    private WorkOrderTypeRepository workOrderTypeRepository;

    @Autowired
    private WorkStepsTypeRepository workStepsTypeRepository;

    @Autowired
    private WorkOrderEntityRepository workOrderEntityRepository;

    @Autowired
    private DocumentSeqRepository documentSeqRepository;

    @Autowired
    private OperationRoleRepository operationRoleRepository;

    @Autowired
    private ProjectTaskTypeRepository projectTaskTypeRepository;

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
                    Map<?, ?> m = mapper.readValue(is, Map.class);

                    Object cid = m.get("companyId");
                    if (cid != null) {
                        companyId = String.valueOf(cid);
                    }

                    Object cname = m.get("companyName");
                    if (cname != null) {
                        companyName = String.valueOf(cname);
                    }

                    if (m.get("language") != null) {
                        language = String.valueOf(m.get("language"));
                    }

                    if (m.get("active") != null) {
                        try {
                            active = Integer.parseInt(String.valueOf(m.get("active"))) != 0;
                        } catch (Exception ex) {
                        }
                    }

                    Object scObj = m.get("showCompany");
                    if (scObj != null) {
                        try {
                            showCompany = Boolean.parseBoolean(String.valueOf(scObj));
                        } catch (Exception ex) {
                        }
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
                    Map<?, ?> m = mapper.readValue(is, Map.class);
                    // tolerate a few key name variants (typos, snake_case, camelCase)
                    Object fn = m.get("firstName");
                    if (fn != null) {
                        firstName = String.valueOf(fn);
                    }

                    Object ln = m.get("lastName");
                    if (ln != null) {
                        lastName = String.valueOf(ln);
                    }

                    Object loginObj = m.get("login");
                    if (loginObj != null) {
                        builderLogin = String.valueOf(loginObj);
                    }

                    if (m.get("password") != null) {
                        password = String.valueOf(m.get("password"));
                    }

                    if (m.get("active") != null) {
                        try {
                            active = Integer.parseInt(String.valueOf(m.get("active")));
                        } catch (Exception ex) {
                        }
                    }

                    if (m.get("level") != null) {
                        try {
                            level = Integer.parseInt(String.valueOf(m.get("level")));
                        } catch (Exception ex) {
                        }
                    }

                    Object cid2 = m.get("companyId");
                    if (cid2 != null) {
                        companyId = String.valueOf(cid2);
                    }
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
        List<Map<String, Object>> roleDefs = new ArrayList<>();
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
                        if (o instanceof Map) {
                            roleDefs.add((Map<String, Object>) o);
                        }
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
                                if (c == '{') {
                                    depth++;
                                } else if (c == '}') {
                                    depth--;
                                }
                                i++;
                                if (depth == 0) {
                                    break;
                                }
                            }
                            if (depth == 0) {
                                String objStr = content.substring(start, i);
                                try {
                                    Map<String, Object> m = mapper.readValue(objStr, Map.class);
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
        if (builderOpt.isPresent()) {
            builder = builderOpt.get();
        }

        List<Role> existingRoles = roleRepository.findAll();
        for (Map<String, Object> rd : roleDefs) {
            String roleName = rd.get("role") != null ? String.valueOf(rd.get("role")) : null;
            if (roleName == null) {
                throw new RuntimeException("[DataInitializer] role.json entry missing required 'role' key; aborting initialization");
            }

            String description = rd.get("description") != null ? String.valueOf(rd.get("description")) : null;
            int level = 1;
            try {
                if (rd.get("level") != null) {
                    level = Integer.parseInt(String.valueOf(rd.get("level")));

                }
            } catch (Exception ex) {
            }
            String menu = rd.get("menu") != null ? String.valueOf(rd.get("menu")) : null;

            Role role = existingRoles.stream().filter(r -> roleName.equals(r.getRole())).findFirst().orElse(null);
            if (role == null) {
                role = new Role();
                role.setRole(roleName);
                if (description != null) {
                    role.setDescription(description);
                }
                role.setLevel(level);
                if (menu != null) {
                    role.setMenu(menu);
                }
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
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object codeObj = m.get("code");
                    Object nameObj = m.get("name");
                    if (codeObj == null) {
                        throw new RuntimeException("[DataInitializer] language.json entry missing required 'code' key; aborting initialization");
                    }
                    String code = String.valueOf(codeObj);
                    String name = nameObj != null ? String.valueOf(nameObj) : null;
                    if (!languageRepository.existsById(code)) {
                        Language lang = new Language();
                        lang.setCode(code);
                        if (name != null) {
                            lang.setName(name);
                        }
                        languageRepository.save(lang);
                        System.out.println("[DataInitializer] Created language " + code + (name != null ? " (" + name + ")" : ""));
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
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object mtObj = m.get("movement");
                    if (mtObj == null) {
                        throw new RuntimeException("[DataInitializer] movement.json entry missing required 'movement' key; aborting initialization");
                    }
                    String movementType = String.valueOf(mtObj).trim();
                    if (movementType.length() == 0) {
                        throw new RuntimeException("[DataInitializer] movement.json entry has empty 'movement' value; aborting initialization");
                    }
                    if (movementType.length() > 1) {
                        movementType = movementType.substring(0, 1);
                    }

                    String movementDescription = m.get("movementDescription") != null ? String.valueOf(m.get("movementDescription")) : null;
                    Integer stockModifier = 0;
                    Integer holdModifier = 0;
                    try {
                        if (m.get("stockModifier") != null) {
                            stockModifier = Integer.parseInt(String.valueOf(m.get("stockModifier")));
                        }
                    } catch (Exception ex) {
                    }
                    try {
                        if (m.get("holdModifier") != null) {
                            holdModifier = Integer.parseInt(String.valueOf(m.get("holdModifier")));
                        }
                    } catch (Exception ex) {
                    }

                    java.util.Optional<StockMovementCode> existingOpt = stockMovementCodeRepository.findById(movementType);
                    if (existingOpt.isPresent()) {
                        StockMovementCode sm = existingOpt.get();
                        if (movementDescription != null) {
                            sm.setMovementDescription(movementDescription);
                        }
                        sm.setStockModifier(stockModifier);
                        sm.setHoldModifier(holdModifier);
                        stockMovementCodeRepository.save(sm);
                        System.out.println("[DataInitializer] Updated stock movement code " + movementType);
                    } else {
                        StockMovementCode sm = new StockMovementCode();
                        sm.setMovementType(movementType);
                        if (movementDescription != null) {
                            sm.setMovementDescription(movementDescription);
                        }
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

        // Load params from initData/param.json:
        // - create missing params using json values
        // - for existing params, keep current value_string and overwrite changeable from json
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/param.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/param.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object keyObj = m.get("param_key");
                    if (keyObj == null) {
                        System.out.println("[DataInitializer] param.json entry missing 'param_key', skipping");
                        continue;
                    }
                    String key = String.valueOf(keyObj).trim();
                    if (key.length() == 0) {
                        System.out.println("[DataInitializer] param.json entry has empty 'param_key', skipping");
                        continue;
                    }

                    // Param now stores a single string value; choose available value fields with priority
                    String valueString = null;
                    Object vs = m.get("value_string");
                    if (vs != null) {
                        valueString = String.valueOf(vs);
                    } else if (m.get("value_long") != null) {
                        valueString = String.valueOf(m.get("value_long"));
                    } else if (m.get("value_decimal") != null) {
                        valueString = String.valueOf(m.get("value_decimal"));
                    } else if (m.get("value_date") != null) {
                        valueString = String.valueOf(m.get("value_date"));
                    }
                    Integer changeable = 0;
                    try {
                        Object changeableObj = m.get("changeable");
                        if (changeableObj instanceof Number number) {
                            changeable = number.intValue();
                        } else if (changeableObj != null) {
                            changeable = Integer.parseInt(changeableObj.toString());
                        }
                    } catch (NumberFormatException ex) {
                    }

                    Optional<Param> existingParam = paramRepository.findById(key);
                    if (existingParam.isPresent()) {
                        Param p = existingParam.get();
                        p.setChangeable(changeable);
                        paramRepository.save(p);
                        System.out.println("[DataInitializer] Updated param " + key + " changeable=" + changeable);
                        continue;
                    }

                    Param p = new Param();
                    p.setParam_key(key);
                    p.setValue_string(valueString);
                    p.setChangeable(changeable);
                    paramRepository.save(p);
                    System.out.println("[DataInitializer] Created param " + key);
                }
            } else {
                System.out.println("[DataInitializer] initData/param.json not found; skipping param import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/param.json: " + ex.getMessage());
        }

        // Load work order types from initData/workordertype.json: insert if not exists
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/workordertype.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/workordertype.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object typeObj = m.get("workOrderType");
                    if (typeObj == null) {
                        throw new RuntimeException("[DataInitializer] workordertype.json entry missing required 'workOrderType' key; aborting initialization");
                    }
                    String type = String.valueOf(typeObj).trim();
                    if (type.length() == 0) {
                        throw new RuntimeException("[DataInitializer] workordertype.json entry has empty 'workOrderType' value; aborting initialization");
                    }

                    java.util.Optional<WorkOrderType> existingTypeOpt = workOrderTypeRepository.findById(type);
                    Integer steps = 1;
                    try {
                        if (m.get("numberOfSteps") != null) {
                            steps = Integer.parseInt(String.valueOf(m.get("numberOfSteps")));
                        }
                    } catch (Exception ex) {
                    }
                    Integer needDetails = 0;
                    try {
                        if (m.get("needDetails") != null) {
                            needDetails = Integer.parseInt(String.valueOf(m.get("needDetails")));
                        }
                    } catch (Exception ex) {
                    }
                    Integer active = 1;
                    try {
                        if (m.get("active") != null) {
                            active = Integer.parseInt(String.valueOf(m.get("active")));
                        }
                    } catch (Exception ex) {
                    }
                    String roleName = null;
                    Object rn = m.get("roleName");
                    if (rn != null) {
                        roleName = String.valueOf(rn).trim();
                        if (roleName.length() == 0) {
                            roleName = null;
                        }
                    }

                    if (existingTypeOpt.isPresent()) {
                        WorkOrderType w = existingTypeOpt.get();
                        Object desc = m.get("workOrderDescription");
                        if (desc != null) {
                            w.setWorkOrderDescription(String.valueOf(desc));
                        }
                        Object ct = m.get("contentType");
                        if (ct != null) {
                            w.setContentType(String.valueOf(ct));
                        }
                        w.setNumberOfSteps(steps);
                        w.setNeedDetails(needDetails);
                        if (roleName != null) {
                            w.setRoleName(roleName);
                        }
                        w.setActive(active);
                        workOrderTypeRepository.save(w);
                        System.out.println("[DataInitializer] Updated work order type " + type);
                    } else {
                        WorkOrderType w = new WorkOrderType();
                        w.setWorkOrderType(type);
                        Object desc = m.get("workOrderDescription");
                        if (desc != null) {
                            w.setWorkOrderDescription(String.valueOf(desc));
                        }
                        Object ct = m.get("contentType");
                        if (ct != null) {
                            w.setContentType(String.valueOf(ct));
                        }
                        w.setNumberOfSteps(steps);
                        w.setNeedDetails(needDetails);
                        if (roleName != null) {
                            w.setRoleName(roleName);
                        }
                        w.setActive(active);
                        workOrderTypeRepository.save(w);
                        System.out.println("[DataInitializer] Created work order type " + type);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/workordertype.json not found; skipping work order type import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/workordertype.json: " + ex.getMessage());
        }

        // Load work steps from initData/workstepstype.json: insert if not exists (by workOrderType + stepNumber)
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/workstepstype.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/workstepstype.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object typeObj = m.get("workOrderType");
                    if (typeObj == null) {
                        throw new RuntimeException("[DataInitializer] workstepstype.json entry missing required 'workOrderType' key; aborting initialization");
                    }
                    String type = String.valueOf(typeObj).trim();
                    if (type.length() == 0) {
                        throw new RuntimeException("[DataInitializer] workstepstype.json entry has empty 'workOrderType' value; aborting initialization");
                    }
                    Integer stepNo = null;
                    try {
                        if (m.get("stepNumber") != null) {
                            stepNo = Integer.parseInt(String.valueOf(m.get("stepNumber")));
                        }
                    } catch (Exception ex) {
                    }
                    if (stepNo == null) {
                        throw new RuntimeException("[DataInitializer] workstepstype.json entry missing required 'stepNumber' key; aborting initialization");
                    }

                    java.util.Optional<WorkStepsType> existingStepOpt = workStepsTypeRepository.findByWorkOrderTypeAndStepNumber(type, stepNo);
                    if (existingStepOpt.isPresent()) {
                        WorkStepsType ws = existingStepOpt.get();
                        Object sd = m.get("stepDescription");
                        if (sd != null) {
                            ws.setStepDescription(String.valueOf(sd));
                        }
                        Object sa = m.get("startAction");
                        if (sa != null) {
                            ws.setStartAction(String.valueOf(sa));
                        }
                        Object sdv = m.get("scanData");
                        if (sdv != null) {
                            try {
                                ws.setScanData(Integer.parseInt(String.valueOf(sdv)));
                            } catch (Exception ex) {
                            }
                        }
                        Object cq = m.get("checkQuantity");
                        if (cq != null) {
                            try {
                                ws.setCheckQuantity(Integer.parseInt(String.valueOf(cq)));
                            } catch (Exception ex) {
                            }
                        }
                        Object ns = m.get("newStock");
                        if (ns != null) {
                            try {
                                ws.setNewStock(Integer.parseInt(String.valueOf(ns)));
                            } catch (Exception ex) {
                            }
                        }
                        Object tp = m.get("takePhoto");
                        if (tp != null) {
                            try {
                                ws.setTakePhoto(Integer.parseInt(String.valueOf(tp)));
                            } catch (Exception ex) {
                            }
                        }
                        Object ea = m.get("endAction");
                        if (ea != null) {
                            ws.setEndAction(String.valueOf(ea));
                        }
                        Object fe = m.get("fromEntity");
                        if (fe != null) {
                            ws.setFromEntity(String.valueOf(fe));
                        }
                        Object te = m.get("toEntity");
                        if (te != null) {
                            ws.setToEntity(String.valueOf(te));
                        }
                        Object nc = m.get("noConfirm");
                        if (nc != null) {
                            try {
                                ws.setNoConfirm(Integer.parseInt(String.valueOf(nc)));
                            } catch (Exception ex) {
                            }
                        }
                        workStepsTypeRepository.save(ws);
                        System.out.println("[DataInitializer] Updated work step for type " + type + " step " + stepNo);
                    } else {
                        WorkStepsType ws = new WorkStepsType();
                        ws.setWorkOrderType(type);
                        ws.setStepNumber(stepNo);
                        Object sd = m.get("stepDescription");
                        if (sd != null) {
                            ws.setStepDescription(String.valueOf(sd));
                        }
                        Object sa = m.get("startAction");
                        if (sa != null) {
                            ws.setStartAction(String.valueOf(sa));
                        }
                        Object sdv = m.get("scanData");
                        if (sdv != null) {
                            try {
                                ws.setScanData(Integer.parseInt(String.valueOf(sdv)));
                            } catch (Exception ex) {
                            }
                        }
                        Object cq = m.get("checkQuantity");
                        if (cq != null) {
                            try {
                                ws.setCheckQuantity(Integer.parseInt(String.valueOf(cq)));
                            } catch (Exception ex) {
                            }
                        }
                        Object ns = m.get("newStock");
                        if (ns != null) {
                            try {
                                ws.setNewStock(Integer.parseInt(String.valueOf(ns)));
                            } catch (Exception ex) {
                            }
                        }
                        Object tp = m.get("takePhoto");
                        if (tp != null) {
                            try {
                                ws.setTakePhoto(Integer.parseInt(String.valueOf(tp)));
                            } catch (Exception ex) {
                            }
                        }
                        Object ea = m.get("endAction");
                        if (ea != null) {
                            ws.setEndAction(String.valueOf(ea));
                        }
                        Object fe = m.get("fromEntity");
                        if (fe != null) {
                            ws.setFromEntity(String.valueOf(fe));
                        }
                        Object te = m.get("toEntity");
                        if (te != null) {
                            ws.setToEntity(String.valueOf(te));
                        }
                        Object nc = m.get("noConfirm");
                        if (nc != null) {
                            try {
                                ws.setNoConfirm(Integer.parseInt(String.valueOf(nc)));
                            } catch (Exception ex) {
                            }
                        }
                        workStepsTypeRepository.save(ws);
                        System.out.println("[DataInitializer] Created work step for type " + type + " step " + stepNo);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/workstepstype.json not found; skipping work steps import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/workstepstype.json: " + ex.getMessage());
        }

        // Load work order entities from initData/workorderentity.json: insert if not exists
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/workorderentity.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/workorderentity.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object idObj = m.get("workOrderEntity");
                    if (idObj == null) {
                        throw new RuntimeException("[DataInitializer] workorderentity.json entry missing required 'workOrderEntity' key; aborting initialization");
                    }
                    String id = String.valueOf(idObj).trim();
                    if (id.length() == 0) {
                        throw new RuntimeException("[DataInitializer] workorderentity.json entry has empty 'workOrderEntity' value; aborting initialization");
                    }

                    java.util.Optional<WorkOrderEntity> existingEntityOpt = workOrderEntityRepository.findById(id);
                    if (existingEntityOpt.isPresent()) {
                        WorkOrderEntity we = existingEntityOpt.get();
                        Object desc = m.get("description");
                        if (desc != null) {
                            we.setDescription(String.valueOf(desc));
                        }
                        workOrderEntityRepository.save(we);
                        System.out.println("[DataInitializer] Updated work order entity " + id);
                    } else {
                        WorkOrderEntity we = new WorkOrderEntity();
                        we.setWorkOrderEntity(id);
                        Object desc = m.get("description");
                        if (desc != null) {
                            we.setDescription(String.valueOf(desc));
                        }
                        workOrderEntityRepository.save(we);
                        System.out.println("[DataInitializer] Created work order entity " + id);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/workorderentity.json not found; skipping work order entity import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/workorderentity.json: " + ex.getMessage());
        }

        // Load document sequences from initData/documentseq.json: ensure each exists
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/documentseq.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/documentseq.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object idObj = m.get("docType");
                    if (idObj == null) {
                        throw new RuntimeException("[DataInitializer] documentseq.json entry missing required 'docType' key; aborting initialization");
                    }
                    String docType = String.valueOf(idObj).trim();
                    if (docType.length() == 0) {
                        throw new RuntimeException("[DataInitializer] documentseq.json entry has empty 'docType' value; aborting initialization");
                    }

                    if (!documentSeqRepository.existsById(docType)) {
                        DocumentSeq ds = new DocumentSeq();
                        ds.setDocType(docType);
                        ds.setSeq(0L);
                        ds.setToken("");
                        documentSeqRepository.save(ds);
                        System.out.println("[DataInitializer] Created document seq " + docType);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/documentseq.json not found; skipping document seq import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/documentseq.json: " + ex.getMessage());
        }

        // Load operation roles from initData/operationrole.json: insert or update by roleName
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/operationrole.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/operationrole.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }
                    java.util.Map m = (java.util.Map) o;
                    Object rn = m.get("roleName");
                    if (rn == null) {
                        throw new RuntimeException("[DataInitializer] operationrole.json entry missing required 'roleName' key; aborting initialization");
                    }
                    String roleName = String.valueOf(rn).trim();
                    if (roleName.length() == 0) {
                        throw new RuntimeException("[DataInitializer] operationrole.json entry has empty 'roleName' value; aborting initialization");
                    }

                    Object desc = m.get("roleDescription");
                    if (desc == null) {
                        desc = m.get("description");
                    }

                    String roleDesc = desc != null ? String.valueOf(desc) : "";

                    java.util.Optional<OperationRole> existing = operationRoleRepository.findById(roleName);
                    if (existing.isPresent()) {
                        OperationRole r = existing.get();
                        r.setRoleDescription(roleDesc);
                        operationRoleRepository.save(r);
                        System.out.println("[DataInitializer] Updated operation role " + roleName);
                    } else {
                        OperationRole r = new OperationRole();
                        r.setRoleName(roleName);
                        r.setRoleDescription(roleDesc);
                        operationRoleRepository.save(r);
                        System.out.println("[DataInitializer] Created operation role " + roleName);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/operationrole.json not found; skipping operation role import");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to read initData/operationrole.json: " + ex.getMessage());
        }

        // Synchronize project task types from initData/projecttasktype.json:
        // - upsert by projectTaskCode
        // - delete DB records not present in JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            ClassPathResource cpr = new ClassPathResource("initData/projecttasktype.json");
            if (cpr.exists()) {
                is = cpr.getInputStream();
            } else {
                File f = new File("initData/projecttasktype.json");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }

            if (is != null) {
                List<?> raw = mapper.readValue(is, List.class);
                Set<String> codesInJson = new HashSet<>();

                for (Object o : raw) {
                    if (!(o instanceof java.util.Map)) {
                        continue;
                    }

                    java.util.Map m = (java.util.Map) o;
                    Object codeObj = m.get("projectTaskCode");
                    if (codeObj == null) {
                        throw new RuntimeException("[DataInitializer] projecttasktype.json entry missing required 'projectTaskCode' key; aborting initialization");
                    }

                    String code = String.valueOf(codeObj).trim();
                    if (code.length() == 0) {
                        throw new RuntimeException("[DataInitializer] projecttasktype.json entry has empty 'projectTaskCode' value; aborting initialization");
                    }

                    codesInJson.add(code);

                    String description = m.get("projectTaskDescription") != null ? String.valueOf(m.get("projectTaskDescription")) : null;
                    Integer userTask = null;
                    try {
                        if (m.get("userTask") != null) {
                            userTask = Integer.parseInt(String.valueOf(m.get("userTask")));
                        }
                    } catch (Exception ex) {
                    }

                    Integer editStartDate = null;
                    try {
                        if (m.get("editStartDate") != null) {
                            editStartDate = Integer.parseInt(String.valueOf(m.get("editStartDate")));
                        }
                    } catch (Exception ex) {
                    }

                    Integer createByStream = null;
                    try {
                        Object createByStreamObj = m.get("createByStream") != null ? m.get("createByStream") : m.get("createStream");
                        if (createByStreamObj != null) {
                            createByStream = Integer.parseInt(String.valueOf(createByStreamObj));
                        }
                    } catch (Exception ex) {
                    }

                    Integer canDelete = null;
                    try {
                        if (m.get("canDelete") != null) {
                            canDelete = Integer.parseInt(String.valueOf(m.get("canDelete")));
                        }
                    } catch (Exception ex) {
                    }

                    Long minimumDays = null;
                    try {
                        if (m.get("minimumDays") != null) {
                            minimumDays = Long.parseLong(String.valueOf(m.get("minimumDays")));
                        }
                    } catch (Exception ex) {
                    }

                    Long maximumDays = null;
                    try {
                        if (m.get("maximumDays") != null) {
                            maximumDays = Long.parseLong(String.valueOf(m.get("maximumDays")));
                        }
                    } catch (Exception ex) {
                    }

                    String alignWith = m.get("alignWith") != null ? String.valueOf(m.get("alignWith")) : null;
                    String inventoryType = m.get("inventoryType") != null ? String.valueOf(m.get("inventoryType")) : null;

                    Integer manpowerRequired = null;
                    try {
                        if (m.get("manpowerRequired") != null) {
                            manpowerRequired = Integer.parseInt(String.valueOf(m.get("manpowerRequired")));
                        }
                    } catch (Exception ex) {
                    }

                    Optional<ProjectTaskType> existingType = projectTaskTypeRepository.findById(code);
                    ProjectTaskType taskType = existingType.orElseGet(ProjectTaskType::new);
                    taskType.setProjectTaskCode(code);
                    taskType.setProjectTaskDescription(description);
                    taskType.setUserTask(userTask);
                    taskType.setEditStartDate(editStartDate);
                    taskType.setCreateByStream(createByStream);
                    taskType.setCanDelete(canDelete);
                    taskType.setMinimumDays(minimumDays);
                    taskType.setMaximumDays(maximumDays);
                    taskType.setAlignWith(alignWith);
                    taskType.setInventoryType(inventoryType);
                    taskType.setManpowerRequired(manpowerRequired);
                    projectTaskTypeRepository.save(taskType);

                    if (existingType.isPresent()) {
                        System.out.println("[DataInitializer] Updated project task type " + code);
                    } else {
                        System.out.println("[DataInitializer] Created project task type " + code);
                    }
                }

                List<ProjectTaskType> existingAllTypes = projectTaskTypeRepository.findAll();
                for (ProjectTaskType existingType : existingAllTypes) {
                    String existingCode = existingType.getProjectTaskCode();
                    if (existingCode != null && !codesInJson.contains(existingCode)) {
                        projectTaskTypeRepository.deleteById(existingCode);
                        System.out.println("[DataInitializer] Deleted project task type not in json: " + existingCode);
                    }
                }
            } else {
                System.out.println("[DataInitializer] initData/projecttasktype.json not found; skipping project task type sync");
            }
        } catch (Exception ex) {
            System.out.println("[DataInitializer] Failed to synchronize initData/projecttasktype.json: " + ex.getMessage());
        }

        // Ensure baselineDate param exists; default to first day of current year at midnight
        if (!paramRepository.existsById("baselineDate")) {
            LocalDate firstDayOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            String baselineDateValue = firstDayOfYear + "T00:00:00";
            Param baselineDate = new Param();
            baselineDate.setParam_key("baselineDate");
            baselineDate.setValue_string(baselineDateValue);
            baselineDate.setChangeable(0);
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
                    String filename = viewScript.getFilename();
                    String viewName = filename != null ? filename.replaceAll("\\.sql$", "") : null;
                    try (java.sql.Statement dropStmt = connection.createStatement()) {
                        if (viewName != null && !viewName.isBlank()) {
                            try {
                                dropStmt.execute("DROP VIEW IF EXISTS " + viewName + " CASCADE");
                            } catch (Exception ex) {
                                // ignore, we'll also try dropping table
                            }
                            try {
                                dropStmt.execute("DROP TABLE IF EXISTS " + viewName + " CASCADE");
                            } catch (Exception ex) {
                                // ignore drop table failures
                            }
                        }
                    }
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
