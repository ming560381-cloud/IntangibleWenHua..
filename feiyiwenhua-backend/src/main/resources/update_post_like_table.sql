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

UPDATE `community_post` p
LEFT JOIN (
  SELECT `post_id`, COUNT(*) AS `like_total`
  FROM `community_post_like`
  GROUP BY `post_id`
) l ON l.`post_id` = p.`id`
SET p.`like_count` = IFNULL(l.`like_total`, 0);
