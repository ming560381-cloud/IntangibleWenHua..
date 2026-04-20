-- 修改community_post表，添加images和coverImage字段
ALTER TABLE community_post ADD COLUMN images VARCHAR(1000) COMMENT '存储图片URL列表，用逗号分隔';
ALTER TABLE community_post ADD COLUMN cover_image VARCHAR(500) COMMENT '封面图片URL';
