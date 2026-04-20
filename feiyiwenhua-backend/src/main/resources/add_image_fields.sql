﻿﻿﻿﻿﻿﻿-- Manual migration script for image-related fields.
-- Keep this file for reference and manual execution only.
-- The application should not run it automatically at startup.

ALTER TABLE `community_post`
ADD COLUMN IF NOT EXISTS `images` VARCHAR(1000) NULL COMMENT '帖子图片，多个图片URL用逗号分隔' AFTER `content`,
ADD COLUMN IF NOT EXISTS `cover_image` VARCHAR(500) NULL COMMENT '封面图片URL' AFTER `images`;

ALTER TABLE `activity`
ADD COLUMN IF NOT EXISTS `images` VARCHAR(1000) NULL COMMENT '活动图片，多个图片URL用逗号分隔' AFTER `cover_image`;

UPDATE `community_post`
SET `images` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20paper%20cutting%20art%20exhibition&image_size=landscape_16_9,https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20lacquer%20art%20workshop&image_size=landscape_16_9',
    `cover_image` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20paper%20cutting%20art%20exhibition&image_size=landscape_16_9'
WHERE `id` = 1;

UPDATE `community_post`
SET `images` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20ink%20painting%20demonstration&image_size=landscape_16_9,https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20calligraphy%20class&image_size=landscape_16_9',
    `cover_image` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20ink%20painting%20demonstration&image_size=landscape_16_9'
WHERE `id` = 2;

UPDATE `community_post`
SET `images` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20ceramic%20art%20exhibition&image_size=landscape_16_9,https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20silk%20weaving%20workshop&image_size=landscape_16_9',
    `cover_image` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20ceramic%20art%20exhibition&image_size=landscape_16_9'
WHERE `id` = 3;

UPDATE `activity`
SET `cover_image` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Intangible%20Cultural%20Heritage%20exhibition%20opening&image_size=landscape_16_9',
    `images` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20art%20exhibition%20hall&image_size=landscape_16_9,https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=visitors%20enjoying%20traditional%20Chinese%20crafts&image_size=landscape_16_9'
WHERE `id` = 1;

UPDATE `activity`
SET `cover_image` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20paper%20cutting%20workshop&image_size=landscape_16_9',
    `images` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=people%20learning%20paper%20cutting&image_size=landscape_16_9,https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=paper%20cutting%20artworks%20display&image_size=landscape_16_9'
WHERE `id` = 2;

UPDATE `activity`
SET `cover_image` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20Chinese%20lacquer%20art%20masterclass&image_size=landscape_16_9',
    `images` = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=lacquer%20art%20materials%20and%20tools&image_size=landscape_16_9,https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=finished%20lacquer%20artworks&image_size=landscape_16_9'
WHERE `id` = 3;
