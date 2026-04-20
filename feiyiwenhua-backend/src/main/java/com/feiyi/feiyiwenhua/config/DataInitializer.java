package com.feiyi.feiyiwenhua.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        upgradeMasterTextColumnsIfNeeded();
        ensureCommunityPostImageColumns();
    }

    private void upgradeMasterTextColumnsIfNeeded() {
        try {
            String achievementsType = jdbcTemplate.queryForObject(
                    "SELECT DATA_TYPE FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'master' AND column_name = 'achievements'",
                    String.class
            );
            if (achievementsType != null && !"mediumtext".equals(achievementsType.toLowerCase(Locale.ROOT))) {
                jdbcTemplate.execute("ALTER TABLE master MODIFY COLUMN achievements MEDIUMTEXT NULL");
            }

            String introductionType = jdbcTemplate.queryForObject(
                    "SELECT DATA_TYPE FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'master' AND column_name = 'introduction'",
                    String.class
            );
            if (introductionType != null && !"text".equals(introductionType.toLowerCase(Locale.ROOT))) {
                jdbcTemplate.execute("ALTER TABLE master MODIFY COLUMN introduction TEXT NULL");
            }
        } catch (Exception ex) {
            // Keep startup non-blocking; table may not exist in fresh env.
            System.err.println("Auto schema upgrade skipped: " + ex.getMessage());
        }
    }

    private void ensureCommunityPostImageColumns() {
        try {
            Integer imagesColumnCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'community_post' AND column_name = 'images'",
                    Integer.class
            );
            if (imagesColumnCount != null && imagesColumnCount == 0) {
                jdbcTemplate.execute("ALTER TABLE community_post ADD COLUMN images VARCHAR(1000) NULL COMMENT '帖子图片，多个URL逗号分隔' AFTER content");
            }

            Integer coverImageColumnCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'community_post' AND column_name = 'cover_image'",
                    Integer.class
            );
            if (coverImageColumnCount != null && coverImageColumnCount == 0) {
                jdbcTemplate.execute("ALTER TABLE community_post ADD COLUMN cover_image VARCHAR(500) NULL COMMENT '封面图片URL' AFTER images");
            }
        } catch (Exception ex) {
            System.err.println("Community image columns check skipped: " + ex.getMessage());
        }
    }
}