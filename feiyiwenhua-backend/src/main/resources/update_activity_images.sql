-- 更新活动图片路径，指向本地 static/activity/ 目录下的图片
-- 请确保以下图片文件存在于 src/main/resources/static/activity/ 目录中：
-- - cover1.png
-- - cover2.png
-- - cover3.png

-- 更新第一条活动记录的图片
UPDATE activity
SET cover_image = '/activity/cover1.png',
    update_time = NOW()
WHERE id = 1;

-- 更新第二条活动记录的图片
UPDATE activity
SET cover_image = '/activity/cover2.png',
    update_time = NOW()
WHERE id = 2;

-- 更新第三条活动记录的图片
UPDATE activity
SET cover_image = '/activity/cover3.png',
    update_time = NOW()
WHERE id = 3;

-- 如果有更多活动记录，可以继续添加更新语句
-- 或者为所有没有cover_image的活动设置默认图片
-- UPDATE activity
-- SET cover_image = '/activity/cover1.png',
--     update_time = NOW()
-- WHERE cover_image IS NULL OR cover_image = '';
