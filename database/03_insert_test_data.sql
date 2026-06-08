-- CampusHub demo data for frontend/backend integration.
-- Demo login: demo / 123456

USE `campushub`;

INSERT INTO `user` (`uuid`, `username`, `password_hash`, `status`, `create_time`, `update_time`) VALUES
('11111111-1111-1111-1111-111111111111', 'demo', 'yFoYdEcYS1yS0gQA2ub3+0B/Qw61MFzNNoygWt3cupS/mxY0Gpit3+IvDW1h9uVb', 1, NOW(), NOW()),
('22222222-2222-2222-2222-222222222222', 'alice', 'yFoYdEcYS1yS0gQA2ub3+0B/Qw61MFzNNoygWt3cupS/mxY0Gpit3+IvDW1h9uVb', 1, NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', 'bob', 'yFoYdEcYS1yS0gQA2ub3+0B/Qw61MFzNNoygWt3cupS/mxY0Gpit3+IvDW1h9uVb', 1, NOW(), NOW());

INSERT INTO `user_detail` (`user_uuid`, `nickname`, `email`, `credit_score`, `school`, `major`, `grade`) VALUES
('11111111-1111-1111-1111-111111111111', 'Demo 用户', 'demo@campushub.local', 100, 'CampusHub University', 'Computer Science', '2026'),
('22222222-2222-2222-2222-222222222222', '张三', 'alice@campushub.local', 98, 'CampusHub University', 'Math', '2026'),
('33333333-3333-3333-3333-333333333333', '李四', 'bob@campushub.local', 96, 'CampusHub University', 'Design', '2025');

INSERT INTO `demand` (`uuid`, `publisher_uuid`, `title`, `location`, `deadline`, `type`, `stat`, `create_time`, `update_time`) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '22222222-2222-2222-2222-222222222222', '求高数期末辅导', '二教自习区', '2026-06-05 20:30:00', 'TUTORING', 'OPEN', '2026-06-01 20:30:00', '2026-06-01 20:30:00'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '33333333-3333-3333-3333-333333333333', '出九成新无线耳机', '图书馆北门', NULL, 'SECONDHAND', 'OPEN', '2026-06-01 18:20:00', '2026-06-01 18:20:00'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', '11111111-1111-1111-1111-111111111111', '今晚帮取东门快递', '东门快递柜', '2026-06-02 21:00:00', 'EXPRESS', 'IN_PROGRESS', '2026-06-01 15:10:00', '2026-06-01 15:10:00'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', '22222222-2222-2222-2222-222222222222', '数学建模组队找队友', '线上 / 创新楼', '2026-06-08 23:59:00', 'TEAM', 'OPEN', '2026-05-31 22:05:00', '2026-05-31 22:05:00');

INSERT INTO `tutoring_demand_detail` (`demand_uuid`, `subject`, `tutoring_mode`, `expected_time`, `duration`, `level_requirement`, `description`) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '高等数学', '线下', '2026-06-03 19:00:00', 120, '讲清极限和导数', '希望找人讲一下极限、导数和常见期末题型。');

INSERT INTO `secondhand_demand_detail` (`demand_uuid`, `item_name`, `category`, `price`, `condition_level`, `trade_location`, `description`) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '无线耳机', '数码', 129.00, '九成新', '图书馆北门', '蓝牙无线耳机，续航正常，适合宿舍和图书馆使用。');

INSERT INTO `express_demand_detail` (`demand_uuid`, `pickup_location`, `delivery_location`, `pickup_code`, `expected_delivery_time`, `description`) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', '东门快递柜', '6 号宿舍楼下', '私聊发送', '2026-06-02 21:00:00', '两个小包裹，希望今晚九点前送到宿舍楼下。');

INSERT INTO `teamup_demand_detail` (`demand_uuid`, `team_goal`, `current_members`, `expected_members`, `deadline`, `contact_method`, `description`) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', '数学建模练习', 2, 3, '2026-06-08 23:59:00', '站内消息', '已有两人，缺一位会 Python 数据处理或论文排版的同学。');
