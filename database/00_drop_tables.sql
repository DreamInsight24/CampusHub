-- CampusHub database cleanup script.
-- Drop tables in dependency order so the script can be run repeatedly.

SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `campushub`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `campushub`;

DROP TABLE IF EXISTS `credit_record`;
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `chat_message`;
DROP TABLE IF EXISTS `conversation`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `task_order`;
DROP TABLE IF EXISTS `demand_favorite`;
DROP TABLE IF EXISTS `demand_application`;
DROP TABLE IF EXISTS `teamup_demand_detail`;
DROP TABLE IF EXISTS `tutoring_demand_detail`;
DROP TABLE IF EXISTS `secondhand_demand_detail`;
DROP TABLE IF EXISTS `express_demand_detail`;
DROP TABLE IF EXISTS `demand`;
DROP TABLE IF EXISTS `user_detail`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;
