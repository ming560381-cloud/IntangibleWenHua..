-- 非遗文化网站社区功能数据库表结构
-- 执行此SQL语句以创建社区相关的表

-- 社区帖子表
CREATE TABLE IF NOT EXISTS `community_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '帖子标题',
  `content` text NOT NULL COMMENT '帖子内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '帖子图片，多个URL以逗号分隔',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '帖子封面图URL',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `author_name` varchar(100) NOT NULL COMMENT '作者名称',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签，逗号分隔',
  `view_count` int DEFAULT '0' COMMENT '浏览数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-草稿，1-已发布，2-隐藏',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子表';

CREATE TABLE IF NOT EXISTS `community_post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_post_like_user_id` (`user_id`),
  KEY `idx_post_like_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子点赞关系表';

-- 社区评论点赞关系表
CREATE TABLE IF NOT EXISTS `community_comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
  KEY `idx_comment_like_user_id` (`user_id`),
  KEY `idx_comment_like_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区评论点赞关系表';

-- 社区评论表
CREATE TABLE IF NOT EXISTS `community_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) NOT NULL COMMENT '用户名称',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID，0表示顶级评论',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-正常，1-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区评论表';

-- 可选：添加外键约束（如果需要）
-- ALTER TABLE `community_post` ADD CONSTRAINT `fk_post_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`);
-- ALTER TABLE `community_comment` ADD CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`);
-- ALTER TABLE `community_comment` ADD CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

-- 插入一些示例数据
INSERT INTO `community_post` (`title`, `content`, `author_id`, `author_name`, `tags`, `view_count`, `like_count`, `comment_count`, `status`) VALUES
('非遗保护的重要性', '非物质文化遗产是一个民族的文化基因，保护非遗就是保护我们的文化根脉。随着现代化进程的加快，许多传统技艺正面临失传的危险。我们应该从多个方面加强非遗保护工作：\n\n1. 加强传承人培养\n2. 推动非遗进校园\n3. 利用数字化技术保存\n4. 发展非遗旅游产业\n\n希望大家一起行动起来，为非遗保护贡献力量！', 1, 'admin', '非遗保护,文化传承', 125, 34, 8, 1),
('传统剪纸艺术的魅力', '剪纸是中国传统的民间艺术，历史悠久，风格独特。今天我去参观了一个剪纸展览，深深被老艺人的精湛技艺所折服。\n\n剪纸不仅是一种技艺，更是一种文化表达。每一幅剪纸作品都蕴含着美好的寓意，如“福”字代表祝福，“鱼”代表年年有余。\n\n现在很多年轻人对剪纸不感兴趣，这种传统技艺面临传承危机。我建议可以开设剪纸体验课程，让更多人了解和喜爱这门艺术。', 3, 'user', '剪纸,传统技艺,民间艺术', 89, 21, 5, 1),
('如何成为一名非遗传承人', '很多人问我如何成为一名非遗传承人，这里分享一些个人经验：\n\n1. 首先要对某项非遗项目有浓厚的兴趣和热情\n2. 寻找合适的师傅，拜师学艺\n3. 坚持长期学习和练习，掌握核心技艺\n4. 参加相关的培训和考核\n5. 积极推广和传播该非遗项目\n\n非遗传承不仅是个人的事，更是社会责任。希望更多年轻人加入传承行列！', 2, 'manager', '非遗传承,经验分享', 156, 42, 12, 1);

INSERT INTO `community_comment` (`post_id`, `user_id`, `user_name`, `content`, `parent_id`, `like_count`) VALUES
(1, 2, 'manager', '说得很好！非遗保护确实需要全社会共同努力。', 0, 5),
(1, 3, 'user', '同意！特别是数字化保存很重要，可以用3D扫描等技术记录传统技艺。', 0, 3),
(1, 1, 'admin', '我们网站也在积极推进非遗数字化工作，欢迎大家提出建议。', 2, 2),
(2, 1, 'admin', '剪纸艺术确实很美，我们计划在下个月举办剪纸工作坊，欢迎大家参加。', 0, 4),
(2, 2, 'manager', '很好的建议！体验课程能让年轻人更容易接受传统艺术。', 4, 1),
(3, 3, 'user', '感谢分享！我一直想学习传统漆艺，不知道哪里有好的师傅？', 0, 2),
(3, 1, 'admin', '我们可以帮你联系漆艺传承人，请关注我们的后续活动通知。', 6, 3);
