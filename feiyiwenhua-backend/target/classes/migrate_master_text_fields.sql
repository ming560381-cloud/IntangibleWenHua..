-- 解决“Data too long for column 'achievements'”问题
-- 在你的 MySQL 库 feiyiwenhua 中执行本脚本

ALTER TABLE master
MODIFY COLUMN introduction TEXT NULL,
MODIFY COLUMN achievements MEDIUMTEXT NULL;
