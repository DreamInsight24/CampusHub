-- H2 schema for integration testing (MySQL compatibility mode)
-- Only includes tables needed for the integration test flows

CREATE TABLE IF NOT EXISTS `user` (
  `uuid` CHAR(36) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `pk_user` PRIMARY KEY (`uuid`),
  CONSTRAINT `uk_user_username` UNIQUE (`username`)
);

CREATE TABLE IF NOT EXISTS `user_detail` (
  `user_uuid` CHAR(36) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `credit_score` INT NOT NULL DEFAULT 100,
  `bio` VARCHAR(500) DEFAULT NULL,
  `school` VARCHAR(100) DEFAULT NULL,
  `major` VARCHAR(100) DEFAULT NULL,
  `grade` VARCHAR(50) DEFAULT NULL,
  `interests` VARCHAR(2000) DEFAULT NULL,
  `tags` VARCHAR(2000) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `pk_user_detail` PRIMARY KEY (`user_uuid`),
  CONSTRAINT `fk_user_detail_user` FOREIGN KEY (`user_uuid`) REFERENCES `user` (`uuid`)
);

CREATE TABLE IF NOT EXISTS `demand` (
  `uuid` CHAR(36) NOT NULL,
  `publisher_uuid` CHAR(36) NOT NULL,
  `taker_uuid` CHAR(36) DEFAULT NULL,
  `title` VARCHAR(100) NOT NULL,
  `location` VARCHAR(100) DEFAULT NULL,
  `deadline` DATETIME DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` DATETIME DEFAULT NULL,
  `type` VARCHAR(50) NOT NULL,
  `stat` VARCHAR(50) NOT NULL DEFAULT 'NO',
  CONSTRAINT `pk_demand` PRIMARY KEY (`uuid`),
  INDEX `idx_demand_publisher_uuid` (`publisher_uuid`),
  INDEX `idx_demand_type` (`type`),
  INDEX `idx_demand_stat` (`stat`)
);

CREATE TABLE IF NOT EXISTS `express_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL,
  `pickup_location` VARCHAR(255) DEFAULT NULL,
  `delivery_location` VARCHAR(255) DEFAULT NULL,
  `pickup_code` VARCHAR(50) DEFAULT NULL,
  `expected_delivery_time` DATETIME DEFAULT NULL,
  `image_urls` VARCHAR(2000) DEFAULT NULL,
  `description` VARCHAR(2000) DEFAULT NULL,
  `dialog_uuid` CHAR(36) DEFAULT NULL,
  CONSTRAINT `pk_express_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_express_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
);

CREATE TABLE IF NOT EXISTS `secondhand_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL,
  `item_name` VARCHAR(100) DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `price` DECIMAL(10,2) DEFAULT NULL,
  `original_price` DECIMAL(10,2) DEFAULT NULL,
  `condition_level` VARCHAR(50) DEFAULT NULL,
  `trade_location` VARCHAR(100) DEFAULT NULL,
  `image_urls` VARCHAR(2000) DEFAULT NULL,
  `description` VARCHAR(2000) DEFAULT NULL,
  `dialog_uuid` CHAR(36) DEFAULT NULL,
  CONSTRAINT `pk_secondhand_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_secondhand_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
);

CREATE TABLE IF NOT EXISTS `tutoring_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL,
  `subject` VARCHAR(100) DEFAULT NULL,
  `tutoring_mode` VARCHAR(50) DEFAULT NULL,
  `expected_time` DATETIME DEFAULT NULL,
  `duration` INT DEFAULT NULL,
  `level_requirement` VARCHAR(100) DEFAULT NULL,
  `image_urls` VARCHAR(2000) DEFAULT NULL,
  `description` VARCHAR(2000) DEFAULT NULL,
  `dialog_uuid` CHAR(36) DEFAULT NULL,
  CONSTRAINT `pk_tutoring_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_tutoring_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
);

CREATE TABLE IF NOT EXISTS `teamup_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL,
  `team_goal` VARCHAR(255) DEFAULT NULL,
  `current_members` INT DEFAULT NULL,
  `expected_members` INT DEFAULT NULL,
  `required_skills` VARCHAR(2000) DEFAULT NULL,
  `deadline` DATETIME DEFAULT NULL,
  `contact_method` VARCHAR(100) DEFAULT NULL,
  `image_urls` VARCHAR(2000) DEFAULT NULL,
  `description` VARCHAR(2000) DEFAULT NULL,
  `dialog_uuid` CHAR(36) DEFAULT NULL,
  CONSTRAINT `pk_teamup_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_teamup_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
);

CREATE TABLE IF NOT EXISTS `conversation` (
  `uuid` CHAR(36) NOT NULL,
  `demand_uuid` CHAR(36) NOT NULL,
  `engagement_uuid` CHAR(36) DEFAULT NULL,
  `owner_id` CHAR(36) NOT NULL,
  `participant_id` CHAR(36) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `pk_conversation` PRIMARY KEY (`uuid`),
  INDEX `idx_conv_demand` (`demand_uuid`)
);

CREATE TABLE IF NOT EXISTS `demand_application` (
  `uuid` CHAR(36) NOT NULL,
  `demand_uuid` CHAR(36) NOT NULL,
  `applicant_uuid` CHAR(36) NOT NULL,
  `statement` VARCHAR(500) DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `pk_demand_application` PRIMARY KEY (`uuid`),
  CONSTRAINT `uk_demand_application_user` UNIQUE (`demand_uuid`, `applicant_uuid`),
  INDEX `idx_demand_application_demand` (`demand_uuid`),
  INDEX `idx_demand_application_applicant` (`applicant_uuid`),
  INDEX `idx_demand_application_status` (`status`)
);

CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` BIGINT AUTO_INCREMENT,
  `conversation_uuid` CHAR(36) NOT NULL,
  `user_uuid` CHAR(36) NOT NULL,
  `message` VARCHAR(2000) NOT NULL,
  `time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_img` TINYINT NOT NULL DEFAULT 0,
  `is_read` TINYINT NOT NULL DEFAULT 0,
  CONSTRAINT `pk_chat_message` PRIMARY KEY (`id`),
  INDEX `idx_cm_conv` (`conversation_uuid`)
);
