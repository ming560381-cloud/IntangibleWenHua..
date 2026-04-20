-- 将历史 manager 角色统一降级为普通用户
-- 手动执行：在目标数据库中运行本脚本

UPDATE user
SET role = 'user'
WHERE role = 'manager';
