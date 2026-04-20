-- 非遗文化网站用户头像字段更新脚本
-- 执行此SQL语句为用户表添加头像字段

-- 为用户表添加avatar字段
ALTER TABLE `user` 
ADD COLUMN `avatar` VARCHAR(500) NULL COMMENT '用户头像URL' AFTER `password`;

-- 更新现有用户数据（可选：为现有用户设置默认头像）
-- 这里我们暂时不设置默认头像，让用户上传或使用首字母占位符

-- 示例：为管理员用户设置一个默认头像（如果需要）
-- UPDATE `user` SET `avatar` = '/images/avatars/admin.jpg' WHERE `username` = 'admin' AND `role` = 'admin';
-- UPDATE `user` SET `avatar` = '/images/avatars/manager.jpg' WHERE `username` = 'manager' AND `role` = 'manager';
-- UPDATE `user` SET `avatar` = '/images/avatars/user.jpg' WHERE `username` = 'user' AND `role` = 'user';

-- 说明：
-- 1. avatar字段存储用户头像的URL地址
-- 2. 可以为空，如果为空则前端显示用户首字母
-- 3. 建议的头像URL格式：/uploads/avatars/{userId}_{timestamp}.jpg
-- 4. 头像文件应保存在服务器端的静态资源目录中