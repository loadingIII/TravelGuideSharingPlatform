-- 更新现有数据的图片路径为前端实际图片
-- 在远程 MySQL 上执行: mysql -u root -p travel_guide_platform < update_img_urls.sql

UPDATE destinations SET cover_image_url = '/img/富士山.jpg' WHERE id = 1;
UPDATE destinations SET cover_image_url = '/img/埃菲尔铁塔.jpg' WHERE id = 2;
UPDATE destinations SET cover_image_url = '/img/长尾船.png' WHERE id = 3;
UPDATE destinations SET cover_image_url = '/img/纽约城市.jpg' WHERE id = 4;
UPDATE destinations SET cover_image_url = '/img/back1.png' WHERE id = 5;
UPDATE destinations SET cover_image_url = '/img/back2.png' WHERE id = 6;
UPDATE destinations SET cover_image_url = '/img/1.jpg' WHERE id = 7;
UPDATE destinations SET cover_image_url = '/img/4.jpg' WHERE id = 8;
UPDATE destinations SET cover_image_url = '/img/新加坡.jpg' WHERE id = 9;
UPDATE destinations SET cover_image_url = '/img/2.jpg' WHERE id = 10;
UPDATE destinations SET cover_image_url = '/img/3.jpg' WHERE id = 11;
UPDATE destinations SET cover_image_url = '/img/希腊圣托尼尼.png' WHERE id = 12;

UPDATE guides SET cover_image_url = '/img/富士山.jpg' WHERE id = 1;
UPDATE guides SET cover_image_url = '/img/埃菲尔铁塔.jpg' WHERE id = 2;
UPDATE guides SET cover_image_url = '/img/长尾船.png' WHERE id = 3;
UPDATE guides SET cover_image_url = '/img/1.jpg' WHERE id = 4;
UPDATE guides SET cover_image_url = '/img/4.jpg' WHERE id = 5;
UPDATE guides SET cover_image_url = '/img/back1.png' WHERE id = 6;
UPDATE guides SET cover_image_url = '/img/2.jpg' WHERE id = 7;
UPDATE guides SET cover_image_url = '/img/纽约城市.jpg' WHERE id = 8;
UPDATE guides SET cover_image_url = '/img/3.jpg' WHERE id = 9;
UPDATE guides SET cover_image_url = '/img/希腊圣托尼尼.png' WHERE id = 10;

UPDATE user_profiles SET avatar_url = '/img/头像1.jpg' WHERE user_id = 1;
UPDATE user_profiles SET avatar_url = '/img/头像2.png' WHERE user_id = 2;
UPDATE user_profiles SET avatar_url = '/img/头像3.png' WHERE user_id = 3;
UPDATE user_profiles SET avatar_url = '/img/头像1.jpg' WHERE user_id = 4;
UPDATE user_profiles SET avatar_url = '/img/头像2.png' WHERE user_id = 5;
UPDATE user_profiles SET avatar_url = '/img/头像3.png' WHERE user_id = 6;
UPDATE user_profiles SET avatar_url = '/img/头像1.jpg' WHERE user_id = 7;
UPDATE user_profiles SET avatar_url = '/img/头像2.png' WHERE user_id = 8;
UPDATE user_profiles SET avatar_url = '/img/头像3.png' WHERE user_id = 9;
UPDATE user_profiles SET avatar_url = '/img/头像1.jpg' WHERE user_id = 10;
UPDATE user_profiles SET avatar_url = '/img/头像2.png' WHERE user_id = 11;
UPDATE user_profiles SET avatar_url = '/img/头像3.png' WHERE user_id = 12;

UPDATE guide_comments SET author_avatar_url = '/img/头像2.png' WHERE id IN (1, 3, 5, 11);
UPDATE guide_comments SET author_avatar_url = '/img/头像3.png' WHERE id IN (2, 7, 8, 10);
UPDATE guide_comments SET author_avatar_url = '/img/头像1.jpg' WHERE id IN (4, 6, 12, 13, 14);
UPDATE guide_comments SET author_avatar_url = '/img/头像2.png' WHERE id = 9;

UPDATE traveler_stories SET author_avatar_url = '/img/头像1.jpg' WHERE id = 1;
UPDATE traveler_stories SET author_avatar_url = '/img/头像3.png' WHERE id = 2;
UPDATE traveler_stories SET author_avatar_url = '/img/头像2.png' WHERE id = 3;
UPDATE traveler_stories SET author_avatar_url = '/img/头像1.jpg' WHERE id = 4;
UPDATE traveler_stories SET author_avatar_url = '/img/头像2.png' WHERE id = 5;
UPDATE traveler_stories SET author_avatar_url = '/img/头像3.png' WHERE id = 6;
UPDATE traveler_stories SET author_avatar_url = '/img/头像1.jpg' WHERE id = 7;
UPDATE traveler_stories SET author_avatar_url = '/img/头像3.png' WHERE id = 8;

UPDATE traveler_story_images SET image_url = '/img/罗马斗兽场.jpg' WHERE story_id = 1 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/悉尼歌剧院.jpg' WHERE story_id = 1 AND sort_order = 2;
UPDATE traveler_story_images SET image_url = '/img/迪拜.jpg' WHERE story_id = 2 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/首尔.jpg' WHERE story_id = 2 AND sort_order = 2;
UPDATE traveler_story_images SET image_url = '/img/看书.jpg' WHERE story_id = 2 AND sort_order = 3;
UPDATE traveler_story_images SET image_url = '/img/徒步旅行.jpg' WHERE story_id = 3 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/巴塞罗亚.png' WHERE story_id = 3 AND sort_order = 2;
UPDATE traveler_story_images SET image_url = '/img/比萨斜塔.png' WHERE story_id = 4 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/back3.png' WHERE story_id = 4 AND sort_order = 2;
UPDATE traveler_story_images SET image_url = '/img/罗马斗兽场.jpg' WHERE story_id = 5 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/悉尼歌剧院.jpg' WHERE story_id = 6 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/首尔.jpg' WHERE story_id = 6 AND sort_order = 2;
UPDATE traveler_story_images SET image_url = '/img/迪拜.jpg' WHERE story_id = 7 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/徒步旅行.jpg' WHERE story_id = 7 AND sort_order = 2;
UPDATE traveler_story_images SET image_url = '/img/巴塞罗亚.png' WHERE story_id = 8 AND sort_order = 1;
UPDATE traveler_story_images SET image_url = '/img/比萨斜塔.png' WHERE story_id = 8 AND sort_order = 2;

UPDATE traveler_story_comments SET author_avatar_url = '/img/头像2.png' WHERE id = 1;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像1.jpg' WHERE id = 2;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 3;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 4;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 5;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像1.jpg' WHERE id = 6;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 7;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 8;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 9;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像1.jpg' WHERE id = 10;
UPDATE traveler_story_comments SET author_avatar_url = '/img/头像3.png' WHERE id = 11;
