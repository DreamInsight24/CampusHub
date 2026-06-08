-- CampusHub core table creation script.
-- Target database: MySQL 8.x

USE `campushub`;

CREATE TABLE IF NOT EXISTS `user` (
  `uuid` CHAR(36) NOT NULL COMMENT 'User UUID',
  `username` VARCHAR(50) NOT NULL COMMENT 'Login username',
  `password_hash` VARCHAR(255) NOT NULL COMMENT 'Hashed password',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  CONSTRAINT `pk_user` PRIMARY KEY (`uuid`),
  CONSTRAINT `uk_user_username` UNIQUE (`username`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='User account table';

CREATE TABLE IF NOT EXISTS `user_detail` (
  `user_uuid` CHAR(36) NOT NULL COMMENT 'Related user UUID',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Display name',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone number',
  `email` VARCHAR(100) DEFAULT NULL COMMENT 'Email address',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT 'Avatar URL',
  `credit_score` INT NOT NULL DEFAULT 100 COMMENT 'Current credit score',
  `bio` VARCHAR(500) DEFAULT NULL COMMENT 'Personal bio',
  `school` VARCHAR(100) DEFAULT NULL COMMENT 'School name',
  `major` VARCHAR(100) DEFAULT NULL COMMENT 'Major',
  `grade` VARCHAR(50) DEFAULT NULL COMMENT 'Grade',
  `interests` JSON DEFAULT NULL COMMENT 'Interest list',
  `tags` JSON DEFAULT NULL COMMENT 'Profile tag list',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  CONSTRAINT `pk_user_detail` PRIMARY KEY (`user_uuid`),
  CONSTRAINT `fk_user_detail_user` FOREIGN KEY (`user_uuid`) REFERENCES `user` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='User profile detail table';

CREATE TABLE IF NOT EXISTS `demand` (
  `uuid` CHAR(36) NOT NULL COMMENT 'Demand UUID',
  `publisher_uuid` CHAR(36) NOT NULL COMMENT 'Publisher user UUID',
  `taker_uuid` CHAR(36) DEFAULT NULL COMMENT 'Taker user UUID',
  `title` VARCHAR(100) NOT NULL COMMENT 'Demand title',
  `location` VARCHAR(100) DEFAULT NULL COMMENT 'Service location',
  `deadline` DATETIME DEFAULT NULL COMMENT 'Deadline',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  `end_time` DATETIME DEFAULT NULL COMMENT 'End time',
  `type` VARCHAR(50) NOT NULL COMMENT 'Demand type',
  `stat` VARCHAR(50) NOT NULL DEFAULT 'NO' COMMENT 'Demand status',
  CONSTRAINT `pk_demand` PRIMARY KEY (`uuid`),
  KEY `idx_demand_publisher_uuid` (`publisher_uuid`),
  KEY `idx_demand_taker_uuid` (`taker_uuid`),
  KEY `idx_demand_type` (`type`),
  KEY `idx_demand_stat` (`stat`),
  KEY `idx_demand_create_time` (`create_time`),
  KEY `idx_demand_type_stat_create_time` (`type`, `stat`, `create_time`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Demand table';

CREATE TABLE IF NOT EXISTS `express_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `pickup_location` VARCHAR(100) DEFAULT NULL COMMENT 'Pickup location',
  `delivery_location` VARCHAR(100) DEFAULT NULL COMMENT 'Delivery location',
  `pickup_code` VARCHAR(50) DEFAULT NULL COMMENT 'Pickup code',
  `expected_delivery_time` DATETIME DEFAULT NULL COMMENT 'Expected delivery time',
  `image_urls` JSON DEFAULT NULL COMMENT 'Image URL list',
  `description` TEXT DEFAULT NULL COMMENT 'Demand detail description',
  `dialog_uuid` CHAR(36) DEFAULT NULL COMMENT 'Related dialog UUID',
  CONSTRAINT `pk_express_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_express_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Express demand detail table';

CREATE TABLE IF NOT EXISTS `secondhand_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `item_name` VARCHAR(100) DEFAULT NULL COMMENT 'Item name',
  `category` VARCHAR(50) DEFAULT NULL COMMENT 'Item category',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT 'Expected selling price',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT 'Original price',
  `condition_level` VARCHAR(50) DEFAULT NULL COMMENT 'Condition level',
  `trade_location` VARCHAR(100) DEFAULT NULL COMMENT 'Trade location',
  `image_urls` JSON DEFAULT NULL COMMENT 'Image URL list',
  `description` TEXT DEFAULT NULL COMMENT 'Demand detail description',
  `dialog_uuid` CHAR(36) DEFAULT NULL COMMENT 'Related dialog UUID',
  CONSTRAINT `pk_secondhand_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_secondhand_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Secondhand demand detail table';

CREATE TABLE IF NOT EXISTS `tutoring_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `subject` VARCHAR(100) DEFAULT NULL COMMENT 'Tutoring subject',
  `tutoring_mode` VARCHAR(50) DEFAULT NULL COMMENT 'Tutoring mode',
  `expected_time` DATETIME DEFAULT NULL COMMENT 'Expected tutoring time',
  `duration` INT DEFAULT NULL COMMENT 'Duration in minutes',
  `level_requirement` VARCHAR(100) DEFAULT NULL COMMENT 'Level requirement',
  `image_urls` JSON DEFAULT NULL COMMENT 'Image URL list',
  `description` TEXT DEFAULT NULL COMMENT 'Demand detail description',
  `dialog_uuid` CHAR(36) DEFAULT NULL COMMENT 'Related dialog UUID',
  CONSTRAINT `pk_tutoring_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_tutoring_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tutoring demand detail table';

CREATE TABLE IF NOT EXISTS `teamup_demand_detail` (
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `team_goal` VARCHAR(255) DEFAULT NULL COMMENT 'Team goal',
  `current_members` INT DEFAULT NULL COMMENT 'Current member count',
  `expected_members` INT DEFAULT NULL COMMENT 'Expected member count',
  `required_skills` JSON DEFAULT NULL COMMENT 'Required skill list',
  `deadline` DATETIME DEFAULT NULL COMMENT 'Teamup deadline',
  `contact_method` VARCHAR(100) DEFAULT NULL COMMENT 'Contact method',
  `image_urls` JSON DEFAULT NULL COMMENT 'Image URL list',
  `description` TEXT DEFAULT NULL COMMENT 'Demand detail description',
  `dialog_uuid` CHAR(36) DEFAULT NULL COMMENT 'Related dialog UUID',
  CONSTRAINT `pk_teamup_demand_detail` PRIMARY KEY (`demand_uuid`),
  CONSTRAINT `fk_teamup_demand_detail_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Teamup demand detail table';

CREATE TABLE IF NOT EXISTS `task_order` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order primary key',
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `publisher_uuid` CHAR(36) NOT NULL COMMENT 'Publisher user UUID',
  `receiver_uuid` CHAR(36) NOT NULL COMMENT 'Receiver user UUID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0 in progress, 1 completed, 2 canceled',
  `accept_time` DATETIME DEFAULT NULL COMMENT 'Accept time',
  `finish_time` DATETIME DEFAULT NULL COMMENT 'Finish time',
  `cancel_time` DATETIME DEFAULT NULL COMMENT 'Cancel time',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  CONSTRAINT `pk_task_order` PRIMARY KEY (`order_id`),
  CONSTRAINT `uk_task_order_demand` UNIQUE (`demand_uuid`),
  KEY `idx_task_order_publisher_uuid` (`publisher_uuid`),
  KEY `idx_task_order_receiver_uuid` (`receiver_uuid`),
  CONSTRAINT `fk_order_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`),
  CONSTRAINT `fk_order_publisher` FOREIGN KEY (`publisher_uuid`) REFERENCES `user` (`uuid`),
  CONSTRAINT `fk_order_receiver` FOREIGN KEY (`receiver_uuid`) REFERENCES `user` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Task order table';

CREATE TABLE IF NOT EXISTS `demand_application` (
  `uuid` CHAR(36) NOT NULL COMMENT 'Application UUID',
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `applicant_uuid` CHAR(36) NOT NULL COMMENT 'Applicant user UUID',
  `statement` VARCHAR(500) DEFAULT NULL COMMENT 'Application statement',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACCEPTED, REJECTED, EXPIRED',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  CONSTRAINT `pk_demand_application` PRIMARY KEY (`uuid`),
  CONSTRAINT `uk_demand_application_user` UNIQUE (`demand_uuid`, `applicant_uuid`),
  KEY `idx_demand_application_demand` (`demand_uuid`),
  KEY `idx_demand_application_applicant` (`applicant_uuid`),
  KEY `idx_demand_application_status` (`status`),
  CONSTRAINT `fk_demand_application_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`),
  CONSTRAINT `fk_demand_application_user` FOREIGN KEY (`applicant_uuid`) REFERENCES `user` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Demand application table';

CREATE TABLE IF NOT EXISTS `review` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Review primary key',
  `order_id` BIGINT NOT NULL COMMENT 'Related order ID',
  `reviewer_uuid` CHAR(36) NOT NULL COMMENT 'Reviewer user UUID',
  `reviewee_uuid` CHAR(36) NOT NULL COMMENT 'Reviewee user UUID',
  `rating` TINYINT NOT NULL COMMENT 'Rating from 1 to 5',
  `content` VARCHAR(500) DEFAULT NULL COMMENT 'Review content',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  CONSTRAINT `pk_review` PRIMARY KEY (`review_id`),
  KEY `idx_review_order_id` (`order_id`),
  KEY `idx_review_reviewer_uuid` (`reviewer_uuid`),
  KEY `idx_review_reviewee_uuid` (`reviewee_uuid`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `task_order` (`order_id`),
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_uuid`) REFERENCES `user` (`uuid`),
  CONSTRAINT `fk_review_reviewee` FOREIGN KEY (`reviewee_uuid`) REFERENCES `user` (`uuid`),
  CONSTRAINT `chk_review_rating` CHECK (`rating` BETWEEN 1 AND 5)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Review table';

CREATE TABLE IF NOT EXISTS `message` (
  `message_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Message primary key',
  `sender_uuid` CHAR(36) DEFAULT NULL COMMENT 'Sender user UUID, NULL means system message',
  `receiver_uuid` CHAR(36) NOT NULL COMMENT 'Receiver user UUID',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT 'Message type',
  `title` VARCHAR(100) DEFAULT NULL COMMENT 'Message title',
  `content` VARCHAR(500) DEFAULT NULL COMMENT 'Message content',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '0 unread, 1 read',
  `related_id` BIGINT DEFAULT NULL COMMENT 'Related business ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  CONSTRAINT `pk_message` PRIMARY KEY (`message_id`),
  KEY `idx_message_sender_uuid` (`sender_uuid`),
  KEY `idx_message_receiver_uuid` (`receiver_uuid`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_uuid`) REFERENCES `user` (`uuid`),
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_uuid`) REFERENCES `user` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Message table';

CREATE TABLE IF NOT EXISTS `credit_record` (
  `credit_record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Credit record primary key',
  `user_uuid` CHAR(36) NOT NULL COMMENT 'Related user UUID',
  `related_order_id` BIGINT DEFAULT NULL COMMENT 'Related order ID',
  `change_value` INT NOT NULL COMMENT 'Credit score change value',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT 'Change reason',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  CONSTRAINT `pk_credit_record` PRIMARY KEY (`credit_record_id`),
  KEY `idx_credit_record_user_uuid` (`user_uuid`),
  KEY `idx_credit_record_related_order_id` (`related_order_id`),
  CONSTRAINT `fk_credit_record_user` FOREIGN KEY (`user_uuid`) REFERENCES `user` (`uuid`),
  CONSTRAINT `fk_credit_record_order` FOREIGN KEY (`related_order_id`) REFERENCES `task_order` (`order_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Credit record table';

CREATE TABLE IF NOT EXISTS `conversation` (
  `uuid` CHAR(36) NOT NULL COMMENT 'Conversation UUID',
  `demand_uuid` CHAR(36) NOT NULL COMMENT 'Related demand UUID',
  `engagement_uuid` CHAR(36) DEFAULT NULL COMMENT 'Related engagement UUID',
  `owner_id` CHAR(36) NOT NULL COMMENT 'Demand publisher user UUID',
  `participant_id` CHAR(36) NOT NULL COMMENT 'Responder user UUID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, CLOSED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  CONSTRAINT `pk_conversation` PRIMARY KEY (`uuid`),
  KEY `idx_conv_demand` (`demand_uuid`),
  KEY `idx_conv_owner` (`owner_id`),
  KEY `idx_conv_participant` (`participant_id`),
  CONSTRAINT `fk_conv_demand` FOREIGN KEY (`demand_uuid`) REFERENCES `demand` (`uuid`),
  CONSTRAINT `fk_conv_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`uuid`),
  CONSTRAINT `fk_conv_participant` FOREIGN KEY (`participant_id`) REFERENCES `user` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Conversation table for real-time messaging';

CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Message primary key',
  `conversation_uuid` CHAR(36) NOT NULL COMMENT 'Related conversation UUID',
  `user_uuid` CHAR(36) NOT NULL COMMENT 'Sender user UUID',
  `message` TEXT NOT NULL COMMENT 'Message content',
  `time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Send time',
  `is_img` TINYINT NOT NULL DEFAULT 0 COMMENT '0 text, 1 image',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '0 unread, 1 read',
  CONSTRAINT `pk_chat_message` PRIMARY KEY (`id`),
  KEY `idx_cm_conv` (`conversation_uuid`),
  KEY `idx_cm_user` (`user_uuid`),
  CONSTRAINT `fk_cm_conv` FOREIGN KEY (`conversation_uuid`) REFERENCES `conversation` (`uuid`),
  CONSTRAINT `fk_cm_user` FOREIGN KEY (`user_uuid`) REFERENCES `user` (`uuid`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Chat message table';
