SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS travel_guide_platform
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE travel_guide_platform;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  phone VARCHAR(20) NULL,
  email VARCHAR(255) NULL,
  password_hash VARCHAR(255) NOT NULL,
  status TINYINT UNSIGNED NOT NULL DEFAULT 1,
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_phone (phone),
  UNIQUE KEY uk_users_email (email),
  KEY idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_profiles (
  user_id BIGINT UNSIGNED NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  avatar_url VARCHAR(500) NULL,
  bio VARCHAR(500) NULL,
  is_vip TINYINT(1) NOT NULL DEFAULT 0,
  guides_count INT UNSIGNED NOT NULL DEFAULT 0,
  followers_count INT UNSIGNED NOT NULL DEFAULT 0,
  likes_received_count INT UNSIGNED NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  CONSTRAINT fk_user_profiles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS destinations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  country VARCHAR(100) NOT NULL,
  city VARCHAR(100) NOT NULL DEFAULT '',
  description VARCHAR(1000) NULL,
  cover_image_url VARCHAR(500) NULL,
  rating_avg DECIMAL(3,2) NULL,
  guides_count INT UNSIGNED NOT NULL DEFAULT 0,
  travelers_count INT UNSIGNED NOT NULL DEFAULT 0,
  popularity_score DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_destinations_country_city_name (country, city, name),
  KEY idx_destinations_country (country),
  KEY idx_destinations_popularity (popularity_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guides (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  destination_id BIGINT UNSIGNED NOT NULL,
  author_id BIGINT UNSIGNED NOT NULL,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(1000) NULL,
  content_html MEDIUMTEXT NULL,
  cover_image_url VARCHAR(500) NULL,
  location_text VARCHAR(200) NULL,
  scope ENUM('domestic','international') NOT NULL DEFAULT 'international',
  travel_mode ENUM('free','group','family','honeymoon','other') NOT NULL DEFAULT 'free',
  publish_status ENUM('draft','published','archived') NOT NULL DEFAULT 'published',
  published_at DATETIME NULL,
  days SMALLINT UNSIGNED NULL,
  budget_total DECIMAL(10,2) NOT NULL DEFAULT 0,
  views_count INT UNSIGNED NOT NULL DEFAULT 0,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
  comments_count INT UNSIGNED NOT NULL DEFAULT 0,
  favorites_count INT UNSIGNED NOT NULL DEFAULT 0,
  status INT DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_guides_destination (destination_id),
  KEY idx_guides_author_published (author_id, published_at),
  KEY idx_guides_scope_mode (scope, travel_mode),
  FULLTEXT KEY ft_guides_search (title, summary, content_html),
  CONSTRAINT fk_guides_destination
    FOREIGN KEY (destination_id) REFERENCES destinations(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_guides_author
    FOREIGN KEY (author_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tags (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tags_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_tags (
  guide_id BIGINT UNSIGNED NOT NULL,
  tag_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (guide_id, tag_id),
  KEY idx_guide_tags_tag (tag_id),
  CONSTRAINT fk_guide_tags_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_guide_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_itinerary_days (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  day_no SMALLINT UNSIGNED NOT NULL,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(1000) NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_itinerary_day (guide_id, day_no),
  KEY idx_itinerary_days_guide (guide_id),
  CONSTRAINT fk_itinerary_days_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_itinerary_spots (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  day_no SMALLINT UNSIGNED NOT NULL,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(1000) NULL,
  image_url VARCHAR(500) NULL,
  time VARCHAR(50) NULL,
  duration VARCHAR(50) NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY idx_itinerary_spots_guide_day (guide_id, day_no),
  CONSTRAINT fk_itinerary_spots_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_comments (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  parent_comment_id BIGINT UNSIGNED NULL,
  author_name VARCHAR(50) NOT NULL,
  author_avatar_url VARCHAR(500) NULL,
  content TEXT NOT NULL,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
  status INT DEFAULT 0,
  is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_guide_comments_guide_created (guide_id, created_at),
  KEY idx_guide_comments_user (user_id),
  KEY idx_guide_comments_parent (parent_comment_id),
  CONSTRAINT fk_guide_comments_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_guide_comments_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_guide_comments_parent
    FOREIGN KEY (parent_comment_id) REFERENCES guide_comments(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_likes (
  guide_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (guide_id, user_id),
  KEY idx_guide_likes_user (user_id),
  CONSTRAINT fk_guide_likes_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_guide_likes_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_favorites (
  guide_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (guide_id, user_id),
  KEY idx_guide_favorites_user (user_id),
  CONSTRAINT fk_guide_favorites_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_guide_favorites_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_comment_likes (
  comment_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id, user_id),
  KEY idx_guide_comment_likes_user (user_id),
  CONSTRAINT fk_guide_comment_likes_comment
    FOREIGN KEY (comment_id) REFERENCES guide_comments(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_guide_comment_likes_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_follows (
  follower_user_id BIGINT UNSIGNED NOT NULL,
  followed_user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (follower_user_id, followed_user_id),
  KEY idx_user_follows_followed (followed_user_id),
  CONSTRAINT fk_user_follows_follower
    FOREIGN KEY (follower_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_user_follows_followed
    FOREIGN KEY (followed_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS traveler_stories (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  author_user_id BIGINT UNSIGNED NULL,
  author_name VARCHAR(50) NOT NULL,
  author_avatar_url VARCHAR(500) NULL,
  is_vip TINYINT(1) NOT NULL DEFAULT 0,
  content TEXT NOT NULL,
  published_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
  comments_count INT UNSIGNED NOT NULL DEFAULT 0,
  shares_count INT UNSIGNED NOT NULL DEFAULT 0,
  status INT DEFAULT 0,
  is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_stories_published (published_at),
  KEY idx_stories_author (author_user_id),
  CONSTRAINT fk_stories_author
    FOREIGN KEY (author_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS traveler_story_images (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  story_id BIGINT UNSIGNED NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_story_images_story (story_id, sort_order),
  CONSTRAINT fk_story_images_story
    FOREIGN KEY (story_id) REFERENCES traveler_stories(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS traveler_story_likes (
  story_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (story_id, user_id),
  KEY idx_story_likes_user (user_id),
  CONSTRAINT fk_story_likes_story
    FOREIGN KEY (story_id) REFERENCES traveler_stories(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_story_likes_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS traveler_story_comments (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  story_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  parent_comment_id BIGINT UNSIGNED NULL,
  author_name VARCHAR(50) NOT NULL,
  author_avatar_url VARCHAR(500) NULL,
  content TEXT NOT NULL,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
  status INT DEFAULT 0,
  is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_story_comments_story_created (story_id, created_at),
  KEY idx_story_comments_user (user_id),
  KEY idx_story_comments_parent (parent_comment_id),
  CONSTRAINT fk_story_comments_story
    FOREIGN KEY (story_id) REFERENCES traveler_stories(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_story_comments_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_story_comments_parent
    FOREIGN KEY (parent_comment_id) REFERENCES traveler_story_comments(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS traveler_story_comment_likes (
  comment_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id, user_id),
  KEY idx_story_comment_likes_user (user_id),
  CONSTRAINT fk_story_comment_likes_comment
    FOREIGN KEY (comment_id) REFERENCES traveler_story_comments(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_story_comment_likes_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  real_name VARCHAR(50) NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'EDITOR',
  status TINYINT UNSIGNED NOT NULL DEFAULT 1,
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_logs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id BIGINT UNSIGNED NOT NULL,
  admin_username VARCHAR(50) NOT NULL,
  action VARCHAR(50) NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT UNSIGNED NULL,
  detail VARCHAR(500) NULL,
  ip_address VARCHAR(50) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_admin_logs_admin_id (admin_id),
  KEY idx_admin_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== 数据插入 =====================

INSERT IGNORE INTO users (id, username, phone, email, password_hash, status, last_login_at) VALUES
(1, 'travel_lover_001', '13800138001', 'travel001@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-29 10:30:00'),
(2, 'wanderlust_002', '13800138002', 'travel002@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-28 15:45:00'),
(3, 'adventure_seeker', '13800138003', 'travel003@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-27 09:20:00'),
(4, 'explorer_pro', '13800138004', 'travel004@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-26 14:10:00'),
(5, 'world_nomad', '13800138005', 'travel005@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-25 11:55:00'),
(6, 'budget_traveler', '13800138006', 'travel006@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-24 16:30:00'),
(7, 'luxury_wanderer', '13800138007', 'travel007@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-23 08:40:00'),
(8, 'solo_adventurer', '13800138008', 'travel008@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-22 13:25:00'),
(9, 'foodie_traveler', '13800138009', 'travel009@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-21 17:15:00'),
(10, 'photo_explorer', '13800138010', 'travel010@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-20 12:00:00'),
(11, 'history_buff', '13800138011', 'travel011@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-19 10:45:00'),
(12, 'nature_lover', '13800138012', 'travel012@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2026-04-18 14:30:00');

INSERT IGNORE INTO user_profiles (user_id, nickname, avatar_url, bio, is_vip, guides_count, followers_count, likes_received_count) VALUES
(1, '旅行达人小明', '/avatars/user1.jpg', '热爱探索世界各地的文化与美食，已完成30+国家旅行', 1, 5, 1250, 3420),
(2, '背包客小红', '/avatars/user2.jpg', '穷游爱好者，用最少的钱走最多的路', 0, 3, 890, 2150),
(3, '探险家阿强', '/avatars/user3.jpg', '极限运动爱好者，挑战世界各地的险峻山峰', 1, 4, 2100, 5680),
(4, '摄影师小陈', '/avatars/user4.jpg', '用镜头记录旅途中的美好瞬间', 0, 6, 1560, 4320),
(5, '美食家小刘', '/avatars/user5.jpg', '走遍世界只为寻找最地道的美食', 1, 2, 780, 1890),
(6, '穷游背包客', '/avatars/user6.jpg', '学生党穷游攻略分享，月均旅行预算2000', 0, 4, 2340, 6780),
(7, '奢华旅行者', '/avatars/user7.jpg', '分享高端酒店和私人定制旅行体验', 1, 3, 1890, 4560),
(8, '独自旅行者', '/avatars/user8.jpg', '一个人的旅行，一个人的精彩', 0, 5, 1230, 3210),
(9, '吃货旅行家', '/avatars/user9.jpg', '为了美食可以飞越半个地球', 1, 2, 670, 1540),
(10, '旅行摄影师', '/avatars/user10.jpg', '专业旅行摄影，记录世界的美', 0, 7, 3450, 8920),
(11, '历史文化迷', '/avatars/user11.jpg', '喜欢探访历史古迹，了解各地文化', 0, 3, 980, 2670),
(12, '自然探索者', '/avatars/user12.jpg', '热爱大自然，徒步登山是我的生活方式', 1, 4, 1560, 4230);

INSERT IGNORE INTO destinations (id, name, country, city, description, cover_image_url, rating_avg, guides_count, travelers_count, popularity_score) VALUES
(1, '东京', '日本', '东京', '日本首都，融合传统与现代的国际大都市，拥有丰富的美食文化和购物体验', '/destinations/tokyo.jpg', 4.85, 15, 2560, 95.50),
(2, '巴黎', '法国', '巴黎', '浪漫之都，艺术与时尚的中心，埃菲尔铁塔和卢浮宫的所在地', '/destinations/paris.jpg', 4.90, 12, 2340, 98.20),
(3, '曼谷', '泰国', '曼谷', '东南亚最受欢迎的旅游城市，以寺庙、美食和夜市闻名', '/destinations/bangkok.jpg', 4.75, 10, 1890, 88.70),
(4, '纽约', '美国', '纽约', '世界金融中心，拥有自由女神像、时代广场等标志性景点', '/destinations/newyork.jpg', 4.80, 8, 2100, 92.30),
(5, '巴厘岛', '印度尼西亚', '巴厘岛', '印度尼西亚著名度假胜地，以海滩、梯田和独特的宗教文化著称', '/destinations/bali.jpg', 4.70, 9, 1670, 85.60),
(6, '伦敦', '英国', '伦敦', '历史悠久的国际都市，大本钟、伦敦眼和白金汉宫必去', '/destinations/london.jpg', 4.75, 7, 1890, 89.40),
(7, '北京', '中国', '北京', '中国首都，拥有故宫、长城等世界文化遗产', '/destinations/beijing.jpg', 4.85, 14, 3200, 96.80),
(8, '上海', '中国', '上海', '中国最大城市，现代与传统交融的国际金融中心', '/destinations/shanghai.jpg', 4.80, 11, 2890, 93.50),
(9, '新加坡', '新加坡', '新加坡', '花园城市，以干净整洁和美食闻名，适合家庭旅游', '/destinations/singapore.jpg', 4.75, 6, 1450, 82.30),
(10, '清迈', '泰国', '清迈', '泰北玫瑰，以古城寺庙、夜市和悠闲氛围著称', '/destinations/chiangmai.jpg', 4.65, 5, 1230, 78.90),
(11, '京都', '日本', '京都', '日本古都，保存完好的寺庙神社和传统日式庭院', '/destinations/kyoto.jpg', 4.80, 8, 1670, 87.50),
(12, '马尔代夫', '马尔代夫', '马累', '印度洋上的珍珠，以水上别墅和清澈海水闻名', '/destinations/maldives.jpg', 4.90, 4, 980, 91.20);

INSERT IGNORE INTO tags (id, name) VALUES
(1, '美食'), (2, '购物'), (3, '文化'), (4, '自然'), (5, '冒险'),
(6, '浪漫'), (7, '家庭'), (8, '摄影'), (9, '历史'), (10, '现代'),
(11, '海滩'), (12, '城市'), (13, '乡村'), (14, '登山'), (15, '潜水');

INSERT IGNORE INTO guides (id, destination_id, author_id, title, summary, content_html, cover_image_url, location_text, scope, travel_mode, publish_status, published_at, days, budget_total, views_count, likes_count, comments_count, favorites_count) VALUES
(1, 1, 1, '东京7日深度游：从浅草寺到涩谷的完美行程', '带你领略东京的传统与现代魅力，包含详细行程安排和美食推荐', '<h1>东京7日深度游</h1><p>东京是一个充满活力的城市...</p>', '/guides/tokyo_guide1.jpg', '东京', 'international', 'free', 'published', '2026-04-15 10:00:00', 7, 15000.00, 12500, 450, 89, 230),
(2, 2, 4, '巴黎5日浪漫之旅：艺术与美食的完美结合', '探索巴黎的艺术殿堂，品尝正宗法式美食', '<h1>巴黎浪漫之旅</h1><p>巴黎是艺术与时尚的中心...</p>', '/guides/paris_guide1.jpg', '巴黎', 'international', 'honeymoon', 'published', '2026-04-10 14:30:00', 5, 18000.00, 9800, 380, 76, 195),
(3, 3, 2, '曼谷6日穷游攻略：人均3000玩转泰国首都', '低预算也能玩转曼谷，省钱又有趣的旅行指南', '<h1>曼谷穷游攻略</h1><p>曼谷是东南亚最受欢迎的旅游城市...</p>', '/guides/bangkok_guide1.jpg', '曼谷', 'international', 'free', 'published', '2026-04-05 09:15:00', 6, 3000.00, 15600, 620, 134, 310),
(4, 7, 3, '北京5日文化之旅：探寻中华文明的瑰宝', '深度体验北京的历史文化，从故宫到长城的完整攻略', '<h1>北京文化之旅</h1><p>北京是中国的首都...</p>', '/guides/beijing_guide1.jpg', '北京', 'domestic', 'family', 'published', '2026-03-28 11:45:00', 5, 8000.00, 18900, 720, 156, 420),
(5, 8, 10, '上海3日现代都市游：感受魔都的独特魅力', '体验上海的现代与传统，从外滩到田子坊', '<h1>上海现代都市游</h1><p>上海是中国最大的城市...</p>', '/guides/shanghai_guide1.jpg', '上海', 'domestic', 'free', 'published', '2026-03-20 16:20:00', 3, 5000.00, 11200, 430, 98, 265),
(6, 5, 5, '巴厘岛7日蜜月之旅：浪漫与自然的完美融合', '适合新婚夫妇的巴厘岛浪漫行程', '<h1>巴厘岛蜜月之旅</h1><p>巴厘岛是印度尼西亚著名的度假胜地...</p>', '/guides/bali_guide1.jpg', '巴厘岛', 'international', 'honeymoon', 'published', '2026-03-15 13:30:00', 7, 25000.00, 8900, 340, 72, 180),
(7, 1, 8, '东京3日快速游：首次访问东京的必看指南', '适合时间有限的东京精华游', '<h1>东京快速游</h1><p>只有3天时间也能玩转东京...</p>', '/guides/tokyo_guide2.jpg', '东京', 'international', 'free', 'published', '2026-03-10 10:00:00', 3, 6000.00, 7800, 290, 54, 145),
(8, 4, 6, '纽约4日经济游：学生党的纽约梦', '低预算玩转纽约，省钱又有趣的攻略', '<h1>纽约经济游</h1><p>纽约是世界金融中心...</p>', '/guides/newyork_guide1.jpg', '纽约', 'international', 'free', 'published', '2026-03-05 15:45:00', 4, 8000.00, 6500, 250, 48, 120),
(9, 11, 11, '京都4日文化游：穿越千年的时光之旅', '深度体验京都的传统文化和历史', '<h1>京都文化游</h1><p>京都是日本的古都...</p>', '/guides/kyoto_guide1.jpg', '京都', 'international', 'free', 'published', '2026-02-28 09:30:00', 4, 10000.00, 5600, 220, 42, 95),
(10, 12, 7, '马尔代夫5日奢华游：水上别墅的极致体验', '享受顶级度假体验，水上别墅和私人沙滩', '<h1>马尔代夫奢华游</h1><p>马尔代夫是印度洋上的珍珠...</p>', '/guides/maldives_guide1.jpg', '马累', 'international', 'honeymoon', 'published', '2026-02-20 14:00:00', 5, 50000.00, 12000, 480, 92, 280);

INSERT IGNORE INTO guide_tags (guide_id, tag_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 12),
(2, 1), (2, 3), (2, 6), (2, 8),
(3, 1), (3, 2), (3, 12),
(4, 3), (4, 9), (4, 7),
(5, 10), (5, 12), (5, 2),
(6, 4), (6, 6), (6, 11),
(7, 1), (7, 12),
(8, 12), (8, 10),
(9, 3), (9, 9),
(10, 4), (10, 6), (10, 11);

INSERT IGNORE INTO guide_itinerary_days (guide_id, day_no, title, summary, sort_order) VALUES
(1, 1, '抵达东京 & 浅草寺', '抵达东京后前往浅草寺，感受传统日式文化', 1),
(1, 2, '涩谷 & 原宿潮流之旅', '探索东京最时尚的街区', 2),
(1, 3, '东京迪士尼乐园', '在迪士尼乐园度过欢乐的一天', 3),
(1, 4, '秋叶原 & 上野', '动漫圣地秋叶原和文化气息浓厚的上野', 4),
(1, 5, '新宿 & 歌舞伎町', '体验东京的夜生活', 5),
(1, 6, '箱根一日游', '前往箱根欣赏富士山美景', 6),
(1, 7, '返回 & 购物', '最后一天购物和整理行李', 7),
(4, 1, '天安门广场 & 故宫', '游览世界最大的城市广场和明清皇宫', 1),
(4, 2, '长城一日游', '登上万里长城，感受古代工程奇迹', 2),
(4, 3, '颐和园 & 圆明园', '欣赏皇家园林的精华', 3),
(4, 4, '南锣鼓巷 & 什刹海', '体验老北京的胡同文化', 4),
(4, 5, '798艺术区 & 三里屯', '感受北京的现代艺术和时尚', 5),
(3, 1, '大皇宫 & 卧佛寺', '参观泰国最著名的佛教建筑', 1),
(3, 2, '暹罗商圈购物', '在曼谷最大的购物中心购物', 2),
(3, 3, '水上市场 & 铁道市场', '体验泰国独特的水上市场文化', 3),
(3, 4, '考山路夜生活', '在考山路感受曼谷的夜生活', 4),
(3, 5, '美功铁道市场', '观看火车穿过市场的奇景', 5),
(3, 6, '返回 & 最后购物', '购买纪念品和特产', 6);


INSERT IGNORE INTO guide_comments (guide_id, user_id, parent_comment_id, author_name, author_avatar_url, content, likes_count) VALUES
(1, 2, NULL, '背包客小红', '/avatars/user2.jpg', '太详细了！正计划去东京，收藏了', 25),
(1, 3, 1, '探险家阿强', '/avatars/user3.jpg', '同求东京攻略，请问迪士尼值得去吗？', 8),
(1, 5, NULL, '美食家小刘', '/avatars/user5.jpg', '筑地市场的海鲜真的太新鲜了，推荐寿司大和大和寿司', 18),
(1, 4, NULL, '摄影师小陈', '/avatars/user4.jpg', '浅草寺的夜景拍照很出片，建议傍晚去', 12),
(2, 5, NULL, '美食家小刘', '/avatars/user5.jpg', '巴黎的餐厅推荐太棒了，下次去一定要试试', 18),
(2, 7, NULL, '奢华旅行者', '/avatars/user7.jpg', '这个预算对于蜜月游来说很合理，已经加入行程单', 12),
(3, 6, NULL, '穷游背包客', '/avatars/user6.jpg', '穷游攻略太实用了，感谢分享！', 32),
(3, 9, NULL, '吃货旅行家', '/avatars/user9.jpg', '考山路的夜市真的太棒了，芒果糯米饭绝了', 20),
(4, 11, NULL, '历史文化迷', '/avatars/user11.jpg', '北京的文化之旅写得很详细，收藏了', 28),
(4, 12, NULL, '自然探索者', '/avatars/user12.jpg', '长城的描述很真实，确实很壮观', 22),
(4, 5, NULL, '美食家小刘', '/avatars/user5.jpg', '北京烤鸭推荐四季民福，比全聚德性价比高', 18),
(6, 7, NULL, '奢华旅行者', '/avatars/user7.jpg', '巴厘岛蜜月太浪漫了，悬岸酒店一定要住', 20),
(10, 1, NULL, '旅行达人小明', '/avatars/user1.jpg', '水上别墅太梦幻了，蜜月首选！', 25),
(10, 7, 48, '奢华旅行者', '/avatars/user7.jpg', '大部分酒店包含早餐和晚餐，午餐另付，也可以升级全包', 8);

INSERT IGNORE INTO guide_likes (guide_id, user_id) VALUES
(1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 3), (2, 5), (2, 6),
(3, 1), (3, 4), (3, 6), (3, 7),
(4, 1), (4, 2), (4, 5), (4, 11),
(5, 1), (5, 3), (5, 6),
(6, 1), (6, 2), (6, 7),
(7, 2), (7, 3), (7, 8),
(8, 1), (8, 6), (8, 8),
(9, 11), (9, 12),
(10, 7), (10, 10);

INSERT IGNORE INTO guide_favorites (guide_id, user_id) VALUES
(1, 2), (1, 3), (1, 5),
(2, 1), (2, 6),
(3, 1), (3, 4), (3, 7),
(4, 1), (4, 2), (4, 11),
(5, 3), (5, 6),
(6, 1), (6, 7),
(7, 2), (7, 8),
(8, 1), (8, 6),
(9, 11), (9, 12),
(10, 7), (10, 10);

INSERT IGNORE INTO guide_comment_likes (comment_id, user_id) VALUES
(1, 3), (1, 4), (1, 5),
(2, 1), (2, 4),
(3, 2), (3, 5),
(4, 1), (4, 6), (4, 9),
(6, 1), (6, 8),
(7, 4), (7, 6),
(8, 3), (8, 5),
(10, 1), (10, 6), (10, 7),
(11, 3), (11, 5),
(12, 4), (12, 6),
(13, 1), (13, 10),
(14, 5), (14, 7),
(15, 1), (15, 4), (15, 8), (15, 9),
(16, 2), (16, 6),
(17, 3), (17, 8),
(18, 1), (18, 3), (18, 12),
(19, 2), (19, 5),
(20, 6), (20, 8),
(21, 1), (21, 5), (21, 12),
(22, 11), (22, 3),
(23, 5), (23, 11),
(24, 1), (24, 3), (24, 8),
(25, 11), (25, 5),
(26, 1), (26, 12);

INSERT IGNORE INTO user_follows (follower_user_id, followed_user_id) VALUES
(2, 1), (3, 1), (4, 1), (5, 1),
(1, 3), (1, 4), (1, 10),
(6, 2), (6, 3),
(7, 1), (7, 10),
(8, 1), (8, 3),
(9, 1), (9, 5),
(11, 4), (11, 1),
(12, 3), (12, 1);

INSERT IGNORE INTO traveler_stories (author_user_id, author_name, author_avatar_url, is_vip, content, published_at, likes_count, comments_count, shares_count) VALUES
(1, '旅行达人小明', '/avatars/user1.jpg', 1, '刚从东京回来，太美了！浅草寺的樱花季真的不容错过。这次尝试了当地的小巷美食，发现了很多隐藏的宝藏餐厅', '2026-04-28 18:30:00', 156, 23, 12),
(3, '探险家阿强', '/avatars/user3.jpg', 1, '今天挑战了北京慕田峪长城，虽然很累但成就感满满！站在长城上俯瞰群山，感觉一切都值得了', '2026-04-27 20:15:00', 234, 45, 28),
(5, '美食家小刘', '/avatars/user5.jpg', 1, '曼谷的街头美食真的太棒了！从芒果糯米饭到泰式奶茶，每一口都是惊喜。强烈推荐大家去考山路逛逛', '2026-04-26 16:45:00', 189, 34, 15),
(10, '旅行摄影师', '/avatars/user10.jpg', 0, '在巴黎埃菲尔铁塔下拍到了最美的日落，这次的摄影作品太满意了。分享几张精选照片给大家', '2026-04-25 21:00:00', 312, 56, 42),
(8, '独自旅行者', '/avatars/user8.jpg', 0, '一个人的旅行也能很精彩！在清迈古城骑行，感受当地人的慢生活，这种自由的感觉太棒了', '2026-04-24 19:30:00', 145, 28, 18),
(6, '穷游背包客', '/avatars/user6.jpg', 0, '学生党也能玩转纽约！分享我的穷游攻略，5天只花了5000元，包含机票住宿和所有开销', '2026-04-23 17:20:00', 278, 67, 35),
(7, '奢华旅行者', '/avatars/user7.jpg', 1, '马尔代夫的水上别墅真的太梦幻了！从阳台直接跳进清澈的海里，这种体验无与伦比', '2026-04-22 15:10:00', 423, 78, 56),
(12, '自然探索者', '/avatars/user12.jpg', 1, '在巴厘岛徒步了阿贡火山，虽然很艰辛，但山顶的日出美到窒息。大自然的力量真的很震撼', '2026-04-21 13:45:00', 198, 32, 22);

INSERT IGNORE INTO traveler_story_images (story_id, image_url, sort_order) VALUES
(1, '/stories/tokyo_sakura1.jpg', 1),
(1, '/stories/tokyo_sakura2.jpg', 2),
(2, '/stories/great_wall1.jpg', 1),
(2, '/stories/great_wall2.jpg', 2),
(2, '/stories/great_wall3.jpg', 3),
(3, '/stories/bangkok_food1.jpg', 1),
(3, '/stories/bangkok_food2.jpg', 2),
(4, '/stories/paris_sunset1.jpg', 1),
(4, '/stories/paris_sunset2.jpg', 2),
(5, '/stories/chiangmai_bike1.jpg', 1),
(6, '/stories/newyork_budget1.jpg', 1),
(6, '/stories/newyork_budget2.jpg', 2),
(7, '/stories/maldives_villa1.jpg', 1),
(7, '/stories/maldives_villa2.jpg', 2),
(8, '/stories/bali_hike1.jpg', 1),
(8, '/stories/bali_hike2.jpg', 2);

INSERT IGNORE INTO traveler_story_likes (story_id, user_id) VALUES
(1, 2), (1, 3), (1, 5),
(2, 1), (2, 4), (2, 12),
(3, 1), (3, 2), (3, 9),
(4, 1), (4, 3), (4, 7),
(5, 1), (5, 6), (5, 10),
(6, 1), (6, 3), (6, 8),
(7, 1), (7, 5), (7, 10),
(8, 1), (8, 3), (8, 5);

INSERT IGNORE INTO traveler_story_comments (story_id, user_id, parent_comment_id, author_name, author_avatar_url, content, likes_count) VALUES
(1, 2, NULL, '背包客小红', '/avatars/user2.jpg', '樱花季真的太美了！请问是几月份去的？', 8),
(1, 1, 1, '旅行达人小明', '/avatars/user1.jpg', '我是3月底4月初去的，正好赶上樱花盛开', 5),
(2, 12, NULL, '自然探索者', '/avatars/user12.jpg', '长城真的很壮观，下次一定要去！', 12),
(3, 9, NULL, '吃货旅行家', '/avatars/user9.jpg', '曼谷的美食真的太诱人了，已经加入旅行清单', 15),
(4, 3, NULL, '探险家阿强', '/avatars/user3.jpg', '这些照片拍得太美了，摄影技术真棒！', 20),
(4, 10, 5, '旅行摄影师', '/avatars/user10.jpg', '谢谢夸奖！那天的光线特别美', 8),
(5, 6, NULL, '穷游背包客', '/avatars/user6.jpg', '一个人的旅行也很精彩，学到了很多', 10),
(6, 3, NULL, '探险家阿强', '/avatars/user3.jpg', '5000元玩纽约太厉害了，请问是怎么做到的？', 18),
(6, 6, 8, '穷游背包客', '/avatars/user6.jpg', '主要是住青旅和吃街头美食，交通用地铁通票', 12),
(7, 1, NULL, '旅行达人小明', '/avatars/user1.jpg', '水上别墅太梦幻了，蜜月首选！', 25),
(8, 3, NULL, '探险家阿强', '/avatars/user3.jpg', '阿贡火山徒步确实很挑战，但值得！', 15);

INSERT IGNORE INTO traveler_story_comment_likes (comment_id, user_id) VALUES
(1, 3), (1, 5), (1, 8),
(2, 6), (2, 10),
(3, 1), (3, 7), (3, 12),
(4, 1), (4, 5),
(5, 1), (5, 4), (5, 7),
(6, 3), (6, 5), (6, 9),
(7, 1), (7, 4), (7, 6),
(8, 1), (8, 6), (8, 10),
(9, 2), (9, 5), (9, 8),
(10, 2), (10, 4), (10, 7);

UPDATE guides SET
  likes_count = (SELECT COUNT(*) FROM guide_likes WHERE guide_id = guides.id),
  favorites_count = (SELECT COUNT(*) FROM guide_favorites WHERE guide_id = guides.id),
  comments_count = (SELECT COUNT(*) FROM guide_comments WHERE guide_id = guides.id AND is_deleted = 0);

UPDATE traveler_stories SET
  likes_count = (SELECT COUNT(*) FROM traveler_story_likes WHERE story_id = traveler_stories.id),
  comments_count = (SELECT COUNT(*) FROM traveler_story_comments WHERE story_id = traveler_stories.id AND is_deleted = 0);

UPDATE user_profiles SET
  guides_count = (SELECT COUNT(*) FROM guides WHERE author_id = user_profiles.user_id AND publish_status = 'published'),
  followers_count = (SELECT COUNT(*) FROM user_follows WHERE followed_user_id = user_profiles.user_id),
  likes_received_count = (SELECT COUNT(*) FROM guide_likes WHERE user_id = user_profiles.user_id);

UPDATE destinations SET
  guides_count = (SELECT COUNT(*) FROM guides WHERE destination_id = destinations.id AND publish_status = 'published'),
  travelers_count = (SELECT COUNT(DISTINCT author_id) FROM guides WHERE destination_id = destinations.id);

SELECT '数据库初始化完成' AS message;
