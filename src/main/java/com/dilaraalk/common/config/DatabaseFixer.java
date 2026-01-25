package com.dilaraalk.common.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatabaseFixer {

    @Bean
    public CommandLineRunner fixDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Drop the restrictive check constraint on orders status
                // This is necessary because Hibernate ddl-auto update didn't update the
                // constraint
                // when we changed the Enum values.
                jdbcTemplate.execute("ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check");
                System.out.println("SUCCESSFULLY DROPPED CONSTRAINT orders_status_check");
            } catch (Exception e) {
                System.out.println("Constraint drop failed (might not exist): " + e.getMessage());
            }
        };
    }
}
