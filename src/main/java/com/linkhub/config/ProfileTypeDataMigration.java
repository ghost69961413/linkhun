package com.linkhub.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Assigns a neutral profile type to profiles that predate role selection. */
@Component
@RequiredArgsConstructor
public class ProfileTypeDataMigration implements ApplicationRunner {
    private static final String VERSION = "20261006_profile_type_default";
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS linkhub_schema_migrations (version VARCHAR(100) PRIMARY KEY, applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        Integer applied = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM linkhub_schema_migrations WHERE version = ?", Integer.class, VERSION);
        if (applied != null && applied > 0) return;

        // Before profile types existed, MySQL filled the newly added ENUM with its
        // first value (CEO). No old profile could have role details, so this safely
        // converts only records that existed before users could choose a type.
        jdbcTemplate.update("UPDATE profiles p LEFT JOIN profile_role_details d ON d.profile_id = p.id SET p.profile_type = 'PROFESSIONAL' WHERE p.profile_type = 'CEO' AND d.profile_id IS NULL");
        jdbcTemplate.update("INSERT INTO linkhub_schema_migrations(version) VALUES (?)", VERSION);
    }
}
