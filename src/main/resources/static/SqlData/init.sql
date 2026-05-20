SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS travel_guide_platform
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE travel_guide_platform;

-- ============================================================
-- 1) 鐢ㄦ埛
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 2) 鐩殑锟?-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 3) 鏀荤暐涓昏〃
-- ============================================================
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
  status INT DEFAULT 0 COMMENT '0=寰呭锟? 1=宸查€氳繃, 2=宸叉嫆锟? 3=宸蹭笅锟?,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 4) 鏍囩
-- ============================================================
CREATE TABLE IF NOT EXISTS tags (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tags_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 5) 琛岀▼缁撴瀯锛堝ぉ -> 鏅偣锟?-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 7) 棰勭畻鎷嗗垎
-- ============================================================

-- ============================================================
-- 8) 鐩稿叧鏀荤暐
-- ============================================================
-- ============================================================
-- 9) 鏀荤暐璇勮
-- ============================================================
CREATE TABLE IF NOT EXISTS guide_comments (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  parent_comment_id BIGINT UNSIGNED NULL,
  author_name VARCHAR(50) NOT NULL,
  author_avatar_url VARCHAR(500) NULL,
  content TEXT NOT NULL,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
  status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 10) 鏀荤暐浜掑姩锛氱偣锟?鏀惰棌/璇勮鐐硅禐
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 11) 鐢ㄦ埛鍏虫敞锛堜綔鑰呭叧娉級
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 12) 鏃呰鑰呮晠浜嬶紙绀惧尯娴侊級
-- ============================================================
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
  status INT DEFAULT 0 COMMENT '0=寰呭锟? 1=宸查€氳繃, 2=宸叉嫆锟? 3=宸蹭笅锟?,
  is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_stories_published (published_at),
  KEY idx_stories_author (author_user_id),
  CONSTRAINT fk_stories_author
    FOREIGN KEY (author_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS traveler_story_comments (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  story_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  parent_comment_id BIGINT UNSIGNED NULL,
  author_name VARCHAR(50) NOT NULL,
  author_avatar_url VARCHAR(500) NULL,
  content TEXT NOT NULL,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
  status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- ============================================================
-- 13) 绠＄悊鍛樿处锟?-- ============================================================
CREATE TABLE IF NOT EXISTS admin_users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  real_name VARCHAR(50) NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'EDITOR' COMMENT 'ROOT=瓒呯骇绠＄悊鍛?ADMIN=绠＄悊鍛?EDITOR=缂栬緫 VIEWER=鏌ョ湅鑰?,
  status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '1=姝ｅ父 0=绂佺敤',
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 榛樿绠＄悊锟?admin/admin123 鐢卞簲鐢ㄥ惎鍔ㄦ椂鑷姩鍒涘缓锛圔Crypt鍔犲瘑锟?
-- ============================================================
-- 14) 绠＄悊鍛樻搷浣滄棩锟?-- ============================================================
CREATE TABLE IF NOT EXISTS admin_logs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id BIGINT UNSIGNED NOT NULL COMMENT '鎿嶄綔绠＄悊鍛業D',
  admin_username VARCHAR(50) NOT NULL COMMENT '鎿嶄綔绠＄悊鍛樼敤鎴峰悕',
  action VARCHAR(50) NOT NULL COMMENT '鎿嶄綔绫诲瀷锛欳REATE/UPDATE/DELETE/LOGIN/LOGOUT',
  target_type VARCHAR(50) NOT NULL COMMENT '鎿嶄綔瀵硅薄绫诲瀷锛歎SER/GUIDE/STORY/DESTINATION/COMMENT',
  target_id BIGINT UNSIGNED NULL COMMENT '鎿嶄綔瀵硅薄ID',
  detail VARCHAR(500) NULL COMMENT '鎿嶄綔璇︽儏',
  ip_address VARCHAR(50) NULL COMMENT '鎿嶄綔IP',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_admin_logs_admin_id (admin_id),
  KEY idx_admin_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 鏁版嵁鎻掑叆
-- ============================================================

-- ============================================================
-- 1) 鐢ㄦ埛鏁版嵁
-- ============================================================
INSERT INTO users (id, username, phone, email, password_hash, status, last_login_at) VALUES
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

-- ============================================================
-- 2) 鐢ㄦ埛妗ｆ
-- ============================================================
INSERT INTO user_profiles (user_id, nickname, avatar_url, bio, is_vip, guides_count, followers_count, likes_received_count) VALUES
(1, '鏃呰杈句汉灏忔槑', '/avatars/user1.jpg', '鐑埍鎺㈢储涓栫晫鍚勫湴鐨勬枃鍖栦笌缇庨锛屽凡瀹屾垚30+鍥藉鏃呰', 1, 5, 1250, 3420),
(2, '鑳屽寘瀹㈠皬锟?, '/avatars/user2.jpg', '绌锋父鐖卞ソ鑰咃紝鐢ㄦ渶灏戠殑閽辫蛋鏈€澶氱殑锟?, 0, 3, 890, 2150),
(3, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '鏋侀檺杩愬姩鐖卞ソ鑰咃紝鎸戞垬涓栫晫鍚勫湴鐨勯櫓宄诲北锟?, 1, 4, 2100, 5680),
(4, '鎽勫奖甯堝皬锟?, '/avatars/user4.jpg', '鐢ㄩ暅澶磋褰曟梾閫斾腑鐨勭編濂界灛锟?, 0, 6, 1560, 4320),
(5, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', '璧伴亶涓栫晫鍙负瀵绘壘鏈€鍦伴亾鐨勭編锟?, 1, 2, 780, 1890),
(6, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '瀛︾敓鍏氱┓娓告敾鐣ュ垎浜紝鏈堝潎鏃呰棰勭畻2000锟?, 0, 4, 2340, 6780),
(7, '濂㈠崕鏃呰锟?, '/avatars/user7.jpg', '鍒嗕韩楂樼閰掑簵鍜岀浜哄畾鍒舵梾琛屼綋锟?, 1, 3, 1890, 4560),
(8, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '涓€涓汉鐨勬梾琛岋紝涓€涓汉鐨勭簿锟?, 0, 5, 1230, 3210),
(9, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '涓轰簡缇庨鍙互椋炶秺鍗婁釜鍦扮悆', 1, 2, 670, 1540),
(10, '鏃呰鎽勫奖锟?, '/avatars/user10.jpg', '涓撲笟鏃呰鎽勫奖锛岃褰曚笘鐣岀殑锟?, 0, 7, 3450, 8920),
(11, '鍘嗗彶鏂囧寲锟?, '/avatars/user11.jpg', '鍠滄鎺㈣鍘嗗彶鍙よ抗锛屼簡瑙ｅ悇鍦版枃锟?, 0, 3, 980, 2670),
(12, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '鐑埍澶ц嚜鐒讹紝寰掓鐧诲北鏄垜鐨勭敓娲绘柟锟?, 1, 4, 1560, 4230);

-- ============================================================
-- 3) 鐩殑鍦版暟锟?-- ============================================================
INSERT INTO destinations (id, name, country, city, description, cover_image_url, rating_avg, guides_count, travelers_count, popularity_score) VALUES
(1, '涓滀含', '鏃ユ湰', '涓滀含', '鏃ユ湰棣栭兘锛岃瀺鍚堜紶缁熶笌鐜颁唬鐨勫浗闄呭ぇ閮藉競锛屾嫢鏈変赴瀵岀殑缇庨鏂囧寲鍜岃喘鐗╀綋锟?, '/destinations/tokyo.jpg', 4.85, 15, 2560, 95.50),
(2, '宸撮粠', '娉曞浗', '宸撮粠', '娴极涔嬮兘锛岃壓鏈笌鏃跺皻鐨勪腑蹇冿紝鍩冭彶灏旈搧濉斿拰鍗㈡诞瀹殑鎵€鍦ㄥ湴', '/destinations/paris.jpg', 4.90, 12, 2340, 98.20),
(3, '鏇艰胺', '娉板浗', '鏇艰胺', '涓滃崡浜氭渶鍙楁杩庣殑鏃呮父鍩庡競锛屼互瀵哄簷銆佺編椋熷拰澶滃競闂诲悕', '/destinations/bangkok.jpg', 4.75, 10, 1890, 88.70),
(4, '绾界害', '缇庡浗', '绾界害', '涓栫晫閲戣瀺涓績锛屾嫢鏈夎嚜鐢卞コ绁炲儚銆佹椂浠ｅ箍鍦虹瓑鏍囧織鎬ф櫙锟?, '/destinations/newyork.jpg', 4.80, 8, 2100, 92.30),
(5, '宸村帢锟?, '鍗板害灏艰タ锟?, '宸村帢锟?, '鍗板凹钁楀悕搴﹀亣鑳滃湴锛屼互娴锋哗銆佹鐢板拰鐙壒鐨勫畻鏁欐枃鍖栬憲锟?, '/destinations/bali.jpg', 4.70, 9, 1670, 85.60),
(6, '浼︽暒', '鑻卞浗', '浼︽暒', '鍘嗗彶鎮犱箙鐨勫浗闄呴兘甯傦紝澶ф湰閽熴€佷鸡鏁︾溂鍜岀櫧閲戞眽瀹繀锟?, '/destinations/london.jpg', 4.75, 7, 1890, 89.40),
(7, '鍖椾含', '涓浗', '鍖椾含', '涓浗棣栭兘锛屾嫢鏈夋晠瀹€侀暱鍩庣瓑涓栫晫鏂囧寲閬椾骇', '/destinations/beijing.jpg', 4.85, 14, 3200, 96.80),
(8, '涓婃捣', '涓浗', '涓婃捣', '涓浗鏈€澶у煄甯傦紝鐜颁唬涓庝紶缁熶氦铻嶇殑鍥介檯閲戣瀺涓績', '/destinations/shanghai.jpg', 4.80, 11, 2890, 93.50),
(9, '鏂板姞锟?, '鏂板姞锟?, '鏂板姞锟?, '鑺卞洯鍩庡競锛屼互骞插噣鏁存磥鍜岀編椋熼椈鍚嶏紝閫傚悎瀹跺涵鏃呮父', '/destinations/singapore.jpg', 4.75, 6, 1450, 82.30),
(10, '娓呰繄', '娉板浗', '娓呰繄', '娉板寳鐜懓锛屼互鍙ゅ煄瀵哄簷銆佸甯傚拰鎮犻棽姘涘洿钁楃О', '/destinations/chiangmai.jpg', 4.65, 5, 1230, 78.90),
(11, '浜兘', '鏃ユ湰', '浜兘', '鏃ユ湰鍙ら兘锛屼繚瀛樺畬濂界殑瀵哄簷绁炵ぞ鍜屼紶缁熸棩寮忓涵锟?, '/destinations/kyoto.jpg', 4.80, 8, 1670, 87.50),
(12, '椹皵浠ｅか', '椹皵浠ｅか', '椹疮', '鍗板害娲嬩笂鐨勭弽鐝狅紝浠ユ按涓婂埆澧呭拰娓呮緢娴锋按闂诲悕', '/destinations/maldives.jpg', 4.90, 4, 980, 91.20);

-- ============================================================
-- ============================================================
-- 5) 鏍囩鏁版嵁
-- ============================================================
INSERT INTO tags (id, name) VALUES
(1, '缇庨'),
(2, '璐墿'),
(3, '鏂囧寲'),
(4, '鑷劧'),
(5, '鍐掗櫓'),
(6, '娴极'),
(7, '瀹跺涵'),
(8, '鎽勫奖'),
(9, '鍘嗗彶'),
(10, '鐜颁唬'),
(11, '娴锋哗'),
(12, '鍩庡競'),
(13, '涔℃潙'),
(14, '鐧诲北'),
(15, '娼滄按');

-- ============================================================
-- 6) 鏀荤暐涓昏〃鏁版嵁
-- ============================================================
INSERT INTO guides (id, destination_id, author_id, title, summary, content_html, cover_image_url, location_text, scope, travel_mode, publish_status, published_at, days, budget_total, views_count, likes_count, comments_count, favorites_count) VALUES
(1, 1, 1, '涓滀含7鏃ユ繁搴︽父锛氫粠娴呰崏瀵哄埌娑╄胺鐨勫畬缇庤锟?, '甯︿綘棰嗙暐涓滀含鐨勪紶缁熶笌鐜颁唬榄呭姏锛屽寘鍚缁嗚绋嬪畨鎺掑拰缇庨鎺ㄨ崘', '<h1>涓滀含7鏃ユ繁搴︽父</h1><p>涓滀含鏄竴涓厖婊℃椿鍔涚殑鍩庡競...</p>', '/guides/tokyo_guide1.jpg', '涓滀含', 'international', 'free', 'published', '2026-04-15 10:00:00', 7, 15000.00, 12500, 450, 89, 230),
(2, 2, 4, '宸撮粠5鏃ユ氮婕箣鏃咃細鑹烘湳涓庣編椋熺殑瀹岀編缁撳悎', '鎺㈢储宸撮粠鐨勮壓鏈鍫傦紝鍝佸懗姝ｅ畻娉曞紡缇庨', '<h1>宸撮粠娴极涔嬫梾</h1><p>宸撮粠鏄壓鏈笌鏃跺皻鐨勪腑锟?..</p>', '/guides/paris_guide1.jpg', '宸撮粠', 'international', 'honeymoon', 'published', '2026-04-10 14:30:00', 5, 18000.00, 9800, 380, 76, 195),
(3, 3, 2, '鏇艰胺6鏃ョ┓娓告敾鐣ワ細浜哄潎3000鐜╄浆娉板浗棣栭兘', '浣庨绠椾篃鑳界帺杞浖璋凤紝鐪侀挶鍙堟湁瓒ｇ殑鏃呰鎸囧崡', '<h1>鏇艰胺绌锋父鏀荤暐</h1><p>鏇艰胺鏄笢鍗椾簹鏈€鍙楁杩庣殑鏃呮父鍩庡競...</p>', '/guides/bangkok_guide1.jpg', '鏇艰胺', 'international', 'free', 'published', '2026-04-05 09:15:00', 6, 3000.00, 15600, 620, 134, 310),
(4, 7, 3, '鍖椾含5鏃ユ枃鍖栦箣鏃咃細鎺㈠涓崕鏂囨槑鐨勭懓锟?, '娣卞害浣撻獙鍖椾含鐨勫巻鍙叉枃鍖栵紝浠庢晠瀹埌闀垮煄鐨勫畬鏁存敾锟?, '<h1>鍖椾含鏂囧寲涔嬫梾</h1><p>鍖椾含鏄腑鍥界殑棣栭兘...</p>', '/guides/beijing_guide1.jpg', '鍖椾含', 'domestic', 'family', 'published', '2026-03-28 11:45:00', 5, 8000.00, 18900, 720, 156, 420),
(5, 8, 10, '涓婃捣3鏃ョ幇浠ｉ兘甯傛父锛氭劅鍙楅瓟閮界殑鐙壒榄呭姏', '浣撻獙涓婃捣鐨勭幇浠ｄ笌浼犵粺锛屼粠澶栨哗鍒扮敯瀛愬潑', '<h1>涓婃捣鐜颁唬閮藉競锟?/h1><p>涓婃捣鏄腑鍥芥渶澶х殑鍩庡競...</p>', '/guides/shanghai_guide1.jpg', '涓婃捣', 'domestic', 'free', 'published', '2026-03-20 16:20:00', 3, 5000.00, 11200, 430, 98, 265),
(6, 5, 5, '宸村帢锟?鏃ヨ湝鏈堜箣鏃咃細娴极涓庤嚜鐒剁殑瀹岀編铻嶅悎', '閫傚悎鏂板澶鐨勫反鍘樺矝娴极琛岀▼', '<h1>宸村帢宀涜湝鏈堜箣锟?/h1><p>宸村帢宀涙槸鍗板凹钁楀悕鐨勫害鍋囪儨锟?..</p>', '/guides/bali_guide1.jpg', '宸村帢锟?, 'international', 'honeymoon', 'published', '2026-03-15 13:30:00', 7, 25000.00, 8900, 340, 72, 180),
(7, 1, 8, '涓滀含3鏃ュ揩閫熸父锛氶娆¤闂笢浜殑蹇呯湅鎸囧崡', '閫傚悎鏃堕棿鏈夐檺鐨勪笢浜簿鍗庢父', '<h1>涓滀含蹇€熸父</h1><p>鍙湁3澶╂椂闂翠篃鑳界帺杞笢锟?..</p>', '/guides/tokyo_guide2.jpg', '涓滀含', 'international', 'free', 'published', '2026-03-10 10:00:00', 3, 6000.00, 7800, 290, 54, 145),
(8, 4, 6, '绾界害4鏃ョ粡娴庢父锛氬鐢熷厷鐨勭航绾︽ⅵ', '浣庨绠楃帺杞航绾︼紝鐪侀挶鍙堟湁瓒ｇ殑鏀荤暐', '<h1>绾界害缁忔祹锟?/h1><p>绾界害鏄笘鐣岄噾铻嶄腑锟?..</p>', '/guides/newyork_guide1.jpg', '绾界害', 'international', 'free', 'published', '2026-03-05 15:45:00', 4, 8000.00, 6500, 250, 48, 120),
(9, 11, 11, '浜兘4鏃ユ枃鍖栨父锛氱┛瓒婂崈骞寸殑鏃跺厜涔嬫梾', '娣卞害浣撻獙浜兘鐨勪紶缁熸枃鍖栧拰鍘嗗彶', '<h1>浜兘鏂囧寲锟?/h1><p>浜兘鏄棩鏈殑鍙ら兘...</p>', '/guides/kyoto_guide1.jpg', '浜兘', 'international', 'free', 'published', '2026-02-28 09:30:00', 4, 10000.00, 5600, 220, 42, 95),
(10, 12, 7, '椹皵浠ｅか5鏃ュア鍗庢父锛氭按涓婂埆澧呯殑鏋佽嚧浣撻獙', '浜彈椤剁骇搴﹀亣浣撻獙锛屾按涓婂埆澧呭拰绉佷汉娌欐哗', '<h1>椹皵浠ｅか濂㈠崕锟?/h1><p>椹皵浠ｅか鏄嵃搴︽磱涓婄殑鐝嶇彔...</p>', '/guides/maldives_guide1.jpg', '椹疮', 'international', 'honeymoon', 'published', '2026-02-20 14:00:00', 5, 50000.00, 12000, 480, 92, 280);

-- ============================================================
-- 7) 鏀荤暐鏍囩鍏宠仈
-- ============================================================
INSERT INTO guide_tags (guide_id, tag_id) VALUES
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

-- ============================================================
-- 8) 鏀荤暐琛岀▼澶╂暟
-- ============================================================
INSERT INTO guide_itinerary_days (guide_id, day_no, title, summary, sort_order) VALUES
(1, 1, '鎶佃揪涓滀含 & 娴呰崏锟?, '鎶佃揪涓滀含鍚庡墠寰€娴呰崏瀵猴紝鎰熷彈浼犵粺鏃ュ紡鏂囧寲', 1),
(1, 2, '娑╄胺 & 鍘熷娼祦涔嬫梾', '鎺㈢储涓滀含鏈€鏃跺皻鐨勮锟?, 2),
(1, 3, '涓滀含杩＋灏间箰锟?, '鍦ㄨ开澹凹涔愬洯搴﹁繃娆箰鐨勪竴锟?, 3),
(1, 4, '绉嬪彾锟?& 涓婇噹', '鍔ㄦ极鍦ｅ湴绉嬪彾鍘熷拰鏂囧寲姘旀伅娴撳帤鐨勪笂锟?, 4),
(1, 5, '鏂板 & 姝岃垶浼庣敽', '浣撻獙涓滀含鐨勫鐢熸椿', 5),
(1, 6, '绠辨牴涓€鏃ユ父', '鍓嶅線绠辨牴娆ｈ祻瀵屽＋灞辩編锟?, 6),
(1, 7, '杩斿洖 & 璐墿', '鏈€鍚庝竴澶╄喘鐗╁拰鏁寸悊琛屾潕', 7),
(4, 1, '澶╁畨闂ㄥ箍锟?& 鏁呭', '娓歌涓栫晫鏈€澶х殑鍩庡競骞垮満鍜屾槑娓呯殗锟?, 1),
(4, 2, '闀垮煄涓€鏃ユ父', '鐧讳笂涓囬噷闀垮煄锛屾劅鍙楀彜浠ｅ伐绋嬪锟?, 2),
(4, 3, '棰愬拰锟?& 鍦嗘槑锟?, '娆ｈ祻鐨囧鍥灄鐨勭簿锟?, 3),
(4, 4, '鍗楅敚榧撳贩 & 浠€鍒规捣', '浣撻獙鑰佸寳浜殑鑳″悓鏂囧寲', 4),
(4, 5, '798鑹烘湳锟?& 涓夐噷锟?, '鎰熷彈鍖椾含鐨勭幇浠ｈ壓鏈拰鏃跺皻', 5),
(3, 1, '澶х殗锟?& 鍗т經锟?, '鍙傝娉板浗鏈€钁楀悕鐨勪經鏁欏缓锟?, 1),
(3, 2, '鏆圭綏鍟嗗湀璐墿', '鍦ㄦ浖璋锋渶澶х殑璐墿涓績璐墿', 2),
(3, 3, '姘翠笂甯傚満 & 閾侀亾甯傚満', '浣撻獙娉板浗鐙壒鐨勬按涓婂競鍦烘枃锟?, 3),
(3, 4, '鑰冨北璺鐢熸椿', '鍦ㄨ€冨北璺劅鍙楁浖璋风殑澶滅敓锟?, 4),
(3, 5, '缇庡姛閾侀亾甯傚満', '瑙傜湅鐏溅绌胯繃甯傚満鐨勫锟?, 5),
(3, 6, '杩斿洖 & 鏈€鍚庤喘锟?, '璐拱绾康鍝佸拰鐗逛骇', 6);

-- ============================================================
-- 12) 鏀荤暐棰勭畻椤圭洰
-- ============================================================
INSERT INTO guide_budget_items (guide_id, category_code, category_name, amount, percentage, sort_order) VALUES
(1, 'flights', '鏈虹エ', 5000.00, 33.33, 1),
(1, 'accommodation', '浣忓', 4500.00, 30.00, 2),
(1, 'food', '椁愰ギ', 2500.00, 16.67, 3),
(1, 'transport', '浜わ拷?, 1500.00, 10.00, 4),
(1, 'shopping', '璐墿', 1000.00, 6.67, 5),
(1, 'tickets', '闂ㄧエ', 500.00, 3.33, 6),
(2, 'flights', '鏈虹エ', 8000.00, 44.44, 1),
(2, 'accommodation', '浣忓', 5000.00, 27.78, 2),
(2, 'food', '椁愰ギ', 3000.00, 16.67, 3),
(2, 'transport', '浜わ拷?, 1000.00, 5.56, 4),
(2, 'shopping', '璐墿', 1000.00, 5.56, 5),
(3, 'flights', '鏈虹エ', 1000.00, 33.33, 1),
(3, 'accommodation', '浣忓', 800.00, 26.67, 2),
(3, 'food', '椁愰ギ', 600.00, 20.00, 3),
(3, 'transport', '浜わ拷?, 300.00, 10.00, 4),
(3, 'shopping', '璐墿', 200.00, 6.67, 5),
(3, 'tickets', '闂ㄧエ', 100.00, 3.33, 6),
(4, 'flights', '鏈虹エ', 2000.00, 25.00, 1),
(4, 'accommodation', '浣忓', 2500.00, 31.25, 2),
(4, 'food', '椁愰ギ', 1500.00, 18.75, 3),
(4, 'transport', '浜わ拷?, 800.00, 10.00, 4),
(4, 'shopping', '璐墿', 700.00, 8.75, 5),
(4, 'tickets', '闂ㄧエ', 500.00, 6.25, 6);

-- ============================================================
-- 14) 鏀荤暐璇勮
-- ============================================================
INSERT INTO guide_comments (guide_id, user_id, parent_comment_id, author_name, author_avatar_url, content, likes_count) VALUES
(1, 2, NULL, '鑳屽寘瀹㈠皬锟?, '/avatars/user2.jpg', '澶缁嗕簡锛佹濂借鍒掑幓涓滀含锛屾敹钘忎簡锟?, 25),
(1, 3, 1, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '鍚屾眰涓滀含鏀荤暐锛岃闂开澹凹鍊煎緱鍘诲悧锟?, 8),
(1, 1, 2, '杩＋灏奸潪甯稿€煎緱鍘伙紝寤鸿瀹夋帓涓€鏁村ぉ锛屾棭璧峰叆鍥帓闃熻兘鐜╂洿澶氶」锟?, 15),
(1, 5, NULL, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', '绛戝湴甯傚満鐨勬捣椴滅湡鐨勫お鏂伴矞浜嗭紝鎺ㄨ崘瀵垮徃澶у拰澶у拰瀵垮徃', 18),
(1, 9, 4, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '鍚屾剰锛佽繕鏈変赴娲插競鍦轰篃涓嶉敊锛屼汉姣旂瓚鍦板皯涓€锟?, 6),
(1, 4, NULL, '鎽勫奖甯堝皬锟?, '/avatars/user4.jpg', '娴呰崏瀵虹殑澶滄櫙鎷嶇収寰堝嚭鐗囷紝寤鸿鍌嶆櫄锟?, 12),
(1, 8, 6, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '涓€涓汉鍘讳笢浜畨鍏ㄥ悧锛熻瑷€涓嶉€氭€庝箞鍔烇紵', 4),
(1, 1, 7, '涓滀含娌诲畨寰堝ソ锛屽湴閾佹湁鑻辨枃鏍囪瘑锛孏oogle Maps瀹屽叏澶熺敤', 9),
(2, 5, NULL, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', '宸撮粠鐨勯鍘呮帹鑽愬お妫掍簡锛屼笅娆″幓涓€瀹氳璇曡瘯', 18),
(2, 7, NULL, '濂㈠崕鏃呰锟?, '/avatars/user7.jpg', '杩欎釜棰勭畻瀵逛簬铚滄湀娓告潵璇村緢鍚堢悊锛屽凡缁忓姞鍏ヨ绋嬪崟', 12),
(2, 10, 10, '鏃呰鎽勫奖锟?, '/avatars/user10.jpg', '鍗㈡诞瀹缓璁鐣欒嚦灏戝崐澶╋紝钂欏涓借帋閭ｈ竟浜鸿秴锟?, 14),
(2, 6, 11, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '璇烽棶宸撮粠鍦伴搧鏂逛究鍚楋紵鏈夋病鏈夋帹鑽愮殑浜ら€氬崱锟?, 5),
(2, 4, 13, '鎽勫奖甯堝皬锟?, '/avatars/user4.jpg', '鎺ㄨ崘涔癙aris Pass锛屽湴閾佸叕浜ゅ崥鐗╅閮借兘锟?, 8),
(3, 6, NULL, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '绌锋父鏀荤暐澶疄鐢ㄤ簡锛屾劅璋㈠垎浜紒', 32),
(3, 8, 15, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '璇烽棶鏇艰胺瀹夊叏鍚楋紵涓€涓汉鍘婚渶瑕佹敞鎰忎粈涔堬紵', 5),
(3, 2, 16, '鑳屽寘瀹㈠皬锟?, '/avatars/user2.jpg', '鏇艰胺鏁翠綋寰堝畨鍏紝灏辨槸娉ㄦ剰灏忓伔鍜岀獊绐佽溅鐮嶄环', 7),
(3, 9, NULL, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '鑰冨北璺殑澶滃競鐪熺殑澶浜嗭紝鑺掓灉绯背楗粷锟?, 20),
(3, 12, 18, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '姘翠笂甯傚満鎺ㄨ崘涓瑰娌欏锛屾棭涓婂幓浜哄皯', 10),
(3, 3, NULL, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '澶х殗瀹€煎緱鍘诲悧锛熸劅瑙夐棬绁ㄦ湁鐐硅吹', 3),
(4, 11, NULL, '鍘嗗彶鏂囧寲锟?, '/avatars/user11.jpg', '鍖椾含鐨勬枃鍖栦箣鏃呭啓寰楀緢璇︾粏锛屾敹钘忎簡', 28),
(4, 12, NULL, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '闀垮煄鐨勬弿杩板緢鐪熷疄锛岀‘瀹炲緢澹', 22),
(4, 1, 21, '鏁呭寤鸿绉熶釜鐢靛瓙璁茶В鍣紝涓嶇劧寰堝鏁呬簨閮戒笉鐭ラ亾', 15),
(4, 5, NULL, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', '鍖椾含鐑ら腑鎺ㄨ崘鍥涘渚垮疁鍧婏紝姣斿叏鑱氬痉鎬т环姣旈珮', 18),
(4, 8, 24, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '渚垮疁鍧婂湪鍝紵闇€瑕佹彁鍓嶉绾﹀悧锟?, 3),
(4, 3, 25, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '鍦ㄥ磭鏂囬棬闄勮繎锛屽缓璁笅锟?鐐瑰幓鎺掗槦', 5),
(5, 1, NULL, '涓婃捣鐨勭幇浠ｉ兘甯傛劅纭疄寰堝己', 15),
(5, 6, NULL, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '鐢板瓙鍧婂拰澶栨哗閮藉緢鍑虹墖锛屾帹鑽愭櫄涓婂幓', 10),
(5, 4, 28, '鎽勫奖甯堝皬锟?, '/avatars/user4.jpg', '澶栨哗澶滄櫙鏈€浣虫媿鎽勬椂闂存槸鏅氫笂7-8鐐癸紝鐏厜鏈€锟?, 12),
(5, 9, NULL, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '涓婃捣鐨勫皬绗煎寘鎺ㄨ崘鍗楃繑棣掑ご搴楋紝鍩庨殟搴欓偅锟?, 8),
(6, 7, NULL, '濂㈠崕鏃呰锟?, '/avatars/user7.jpg', '宸村帢宀涜湝鏈堝お娴极浜嗭紝鎮礀閰掑簵涓€瀹氳锟?, 20),
(6, 1, 32, '璇烽棶鎮礀閰掑簵澶ф浠€涔堜环浣嶏紵闇€瑕佹彁鍓嶅涔呴璁紵', 6),
(6, 5, 33, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', '鎺ㄨ崘涔屽叞宸村浘鐨勬偓宕栭厭搴楋紝鎻愬墠1-2涓湀棰勮锛屼环锟?000-5000/锟?, 9),
(6, 11, NULL, '鍘嗗彶鏂囧寲锟?, '/avatars/user11.jpg', '娴风搴欐棩钀界湡鐨勫緢缇庯紝寤鸿涓嬪崍4鐐瑰墠锟?, 14),
(6, 8, 35, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '涓€涓汉鍘诲反鍘樺矝鍚堥€傚悧锛熻繕鏄洿閫傚悎鎯呬荆锟?, 4),
(7, 3, NULL, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '3澶╀篃鑳界帺涓滀含锛熻绋嬪畨鎺掑緱寰堢揣锟?, 10),
(7, 6, 37, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '鏃堕棿绱х殑璇濇帹鑽愪拱鍦伴搧涓€鏃ュ埜锛岃兘鐪佷笉灏戦挶', 8),
(7, 9, NULL, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '3澶╃殑璇濆缓璁妸娴呰崏鍜屾订璋锋斁鍚屼竴澶╋紝鑺傜渷鏃堕棿', 6),
(8, 1, NULL, '瀛︾敓鍏氫篃鑳界帺绾界害锛屽お鍔卞織锟?, 12),
(8, 8, 40, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '闈掓梾鎺ㄨ崘鍝锛熷畨鍏ㄥ悧锟?, 5),
(8, 6, 41, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '鎺ㄨ崘Hostelling International鐨勭航绾﹀垎搴楋紝浣嶇疆濂戒环鏍间究锟?, 7),
(8, 12, NULL, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '涓ぎ鍏洯鍏嶈垂鍙堝ソ鐜╋紝鍊煎緱鑺卞崐澶╂椂锟?, 9),
(9, 1, NULL, '浜兘鐨勫搴欑湡鐨勫緢鏈夐煹鍛筹紝娓呮按瀵哄繀锟?, 15),
(9, 4, 44, '鎽勫奖甯堝皬锟?, '/avatars/user4.jpg', '娓呮按瀵哄缓璁棭涓婂幓锛屼汉灏戝厜绾垮ソ', 8),
(9, 12, NULL, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '宀氬北绔规灄澶編浜嗭紝鎺ㄨ崘绉熻嚜琛岃溅娓歌', 12),
(9, 9, 46, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '浜兘鎶硅尪鐢滅偣鎺ㄨ崘涓潙钘ゅ悏锛屽畤娌绘湰搴楁渶姝ｅ畻', 10),
(10, 1, NULL, '姘翠笂鍒澶ⅵ骞讳簡锛岃湝鏈堥閫夛紒', 25),
(10, 5, 48, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', '璇烽棶閰掑簵鍖呴鍚楋紵杩樻槸闇€瑕佸彟澶栦粯璐癸紵', 6),
(10, 7, 49, '濂㈠崕鏃呰锟?, '/avatars/user7.jpg', '澶ч儴鍒嗗椁愬寘鍚棭椁愬拰鏅氶锛屽崍椁愬彟浠橈紝涔熷彲浠ュ崌绾у叏锟?, 8),
(10, 3, NULL, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '椹皵浠ｅか娴綔鐪熺殑鑳界湅鍒板緢澶氶奔鍚楋紵', 10),
(10, 12, 51, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '姘翠笅鐢熸€佸緢涓板瘜锛屾帹鑽愬甫闃叉按鐩告満鎷嶇収', 7),
(10, 8, NULL, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', '涓€涓汉鍘婚┈灏斾唬澶細涓嶄細澶吹浜嗭紵', 4);

-- ============================================================
-- 15) 鏀荤暐鐐硅禐
-- ============================================================
INSERT INTO guide_likes (guide_id, user_id) VALUES
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

-- ============================================================
-- 16) 鏀荤暐鏀惰棌
-- ============================================================
INSERT INTO guide_favorites (guide_id, user_id) VALUES
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

-- ============================================================
-- 17) 鏀荤暐璇勮鐐硅禐
-- ============================================================
INSERT INTO guide_comment_likes (comment_id, user_id) VALUES
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
(26, 1), (26, 12),
(28, 3), (28, 6), (28, 9),
(29, 1), (29, 4),
(30, 3), (30, 6),
(31, 1), (31, 4),
(32, 1), (32, 5), (32, 11),
(33, 7), (33, 5),
(34, 1), (34, 7),
(35, 5), (35, 7),
(36, 1), (36, 5),
(37, 6), (37, 9),
(38, 3), (38, 9),
(39, 3), (39, 6),
(40, 6), (40, 8), (40, 12),
(41, 1), (41, 6),
(42, 1), (42, 8),
(43, 1), (43, 6),
(44, 4), (44, 12),
(45, 1), (45, 12),
(46, 1), (46, 4), (46, 9),
(47, 1), (47, 12),
(48, 3), (48, 5), (48, 7), (48, 12),
(49, 1), (49, 7),
(50, 1), (50, 3),
(51, 1), (51, 5), (51, 7),
(52, 3), (52, 7),
(53, 1), (53, 5);

-- ============================================================
-- 18) 鐢ㄦ埛鍏虫敞
-- ============================================================
INSERT INTO user_follows (follower_user_id, followed_user_id) VALUES
(2, 1), (3, 1), (4, 1), (5, 1),
(1, 3), (1, 4), (1, 10),
(6, 2), (6, 3),
(7, 1), (7, 10),
(8, 1), (8, 3),
(9, 1), (9, 5),
(11, 4), (11, 1),
(12, 3), (12, 1);

-- ============================================================
-- 19) 鏃呰鑰呮晠锟?-- ============================================================
INSERT INTO traveler_stories (author_user_id, author_name, author_avatar_url, is_vip, content, published_at, likes_count, comments_count, shares_count) VALUES
(1, '鏃呰杈句汉灏忔槑', '/avatars/user1.jpg', 1, '鍒氬垰浠庝笢浜洖鏉ワ紝澶編浜嗭紒娴呰崏瀵虹殑妯辫姳瀛ｇ湡鐨勪笉瀹归敊杩囥€傝繖娆″皾璇曚簡褰撳湴鐨勫皬宸风編椋燂紝鍙戠幇浜嗗緢澶氶殣钘忕殑瀹濊棌椁愬巺锟?, '2026-04-28 18:30:00', 156, 23, 12),
(3, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', 1, '浠婂ぉ鎸戞垬浜嗗寳浜厱鐢板唱闀垮煄锛岃櫧鐒跺緢绱絾鎴愬氨鎰熸弧婊★紒绔欏湪闀垮煄涓婁刊鐬扮兢灞憋紝鎰熻涓€鍒囬兘鍊煎緱浜嗭拷?, '2026-04-27 20:15:00', 234, 45, 28),
(5, '缇庨瀹跺皬锟?, '/avatars/user5.jpg', 1, '鏇艰胺鐨勮澶寸編椋熺湡鐨勫お妫掍簡锛佷粠鑺掓灉绯背楗埌娉板紡濂惰尪锛屾瘡涓€鍙ｉ兘鏄儕鍠溿€傚己鐑堟帹鑽愬ぇ瀹跺幓鑰冨北璺€涢€涳拷?, '2026-04-26 16:45:00', 189, 34, 15),
(10, '鏃呰鎽勫奖锟?, '/avatars/user10.jpg', 0, '鍦ㄥ反榛庡焹鑿插皵閾佸涓嬫媿鍒颁簡鏈€缇庣殑鏃ヨ惤锛岃繖娆＄殑鎽勫奖浣滃搧澶弧鎰忎簡銆傚垎浜嚑寮犵簿閫夌収鐗囩粰澶у锟?, '2026-04-25 21:00:00', 312, 56, 42),
(8, '鐙嚜鏃呰锟?, '/avatars/user8.jpg', 0, '涓€涓汉鐨勬梾琛屼篃鑳藉緢绮惧僵锛佸湪娓呰繄鍙ゅ煄楠戣锛屾劅鍙楀綋鍦颁汉鐨勬參鐢熸椿锛岃繖绉嶈嚜鐢辩殑鎰熻澶浜嗭拷?, '2026-04-24 19:30:00', 145, 28, 18),
(6, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', 0, '瀛︾敓鍏氫篃鑳界帺杞航绾︼紒鍒嗕韩鎴戠殑绌锋父鏀荤暐锟?澶╁彧鑺变簡5000鍏冿紝鍖呭惈鏈虹エ浣忓鍜屾墍鏈夊紑閿€锟?, '2026-04-23 17:20:00', 278, 67, 35),
(7, '濂㈠崕鏃呰锟?, '/avatars/user7.jpg', 1, '椹皵浠ｅか鐨勬按涓婂埆澧呯湡鐨勫お姊﹀够浜嗭紒浠庨槼鍙扮洿鎺ヨ烦杩涙竻婢堢殑娴烽噷锛岃繖绉嶄綋楠屾棤涓庝鸡姣旓拷?, '2026-04-22 15:10:00', 423, 78, 56),
(12, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', 1, '鍦ㄥ反鍘樺矝寰掓浜嗛樋璐＄伀灞憋紝铏界劧寰堣壈杈涳紝浣嗗北椤剁殑鏃ュ嚭缇庡埌绐掓伅銆傚ぇ鑷劧鐨勫姏閲忕湡鐨勫緢闇囨捈锟?, '2026-04-21 13:45:00', 198, 32, 22);

-- ============================================================
-- 20) 鏃呰鑰呮晠浜嬪浘锟?-- ============================================================
INSERT INTO traveler_story_images (story_id, image_url, sort_order) VALUES
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

-- ============================================================
-- 21) 鏃呰鑰呮晠浜嬬偣锟?-- ============================================================
INSERT INTO traveler_story_likes (story_id, user_id) VALUES
(1, 2), (1, 3), (1, 5),
(2, 1), (2, 4), (2, 12),
(3, 1), (3, 2), (3, 9),
(4, 1), (4, 3), (4, 7),
(5, 1), (5, 6), (5, 10),
(6, 1), (6, 3), (6, 8),
(7, 1), (7, 5), (7, 10),
(8, 1), (8, 3), (8, 5);

-- ============================================================
-- 22) 鏃呰鑰呮晠浜嬭瘎锟?-- ============================================================
INSERT INTO traveler_story_comments (story_id, user_id, parent_comment_id, author_name, author_avatar_url, content, likes_count) VALUES
(1, 2, NULL, '鑳屽寘瀹㈠皬锟?, '/avatars/user2.jpg', '妯辫姳瀛ｇ湡鐨勫お缇庝簡锛佽闂槸鍑犳湀浠藉幓鐨勶紵', 8),
(1, 1, 1, '鎴戞槸3鏈堝簳4鏈堝垵鍘荤殑锛屾濂借刀涓婃ū鑺辩洓寮€', 5),
(2, 12, NULL, '鑷劧鎺㈢储锟?, '/avatars/user12.jpg', '闀垮煄鐪熺殑寰堝．瑙傦紝涓嬫涓€瀹氳鍘伙紒', 12),
(3, 9, NULL, '鍚冭揣鏃呰锟?, '/avatars/user9.jpg', '鏇艰胺鐨勭編椋熺湡鐨勫お璇变汉浜嗭紝宸茬粡鍔犲叆鏃呰娓呭崟', 15),
(4, 3, NULL, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '杩欎簺鐓х墖鎷嶅緱澶編浜嗭紝鎽勫奖鎶€鏈湡妫掞紒', 20),
(4, 10, 5, '鏃呰鎽勫奖锟?, '/avatars/user10.jpg', '璋㈣阿澶稿锛侀偅澶╃殑鍏夌嚎鐗瑰埆锟?, 8),
(5, 6, NULL, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '涓€涓汉鐨勬梾琛屼篃寰堢簿褰╋紝瀛﹀埌浜嗗緢锟?, 10),
(6, 3, NULL, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '5000鍏冪帺绾界害澶帀瀹充簡锛岃闂槸鎬庝箞鍋氬埌鐨勶紵', 18),
(6, 6, 8, '绌锋父鑳屽寘锟?, '/avatars/user6.jpg', '涓昏鏄綇闈掓梾鍜屽悆琛楀ご缇庨锛屼氦閫氱敤鍦伴搧閫氱エ', 12),
(7, 1, NULL, '姘翠笂鍒澶ⅵ骞讳簡锛岃湝鏈堥閫夛紒', 25),
(8, 3, NULL, '鎺㈤櫓瀹堕樋锟?, '/avatars/user3.jpg', '闃胯础鐏北寰掓纭疄寰堟寫鎴橈紝浣嗗€煎緱锟?, 15);

-- ============================================================
-- 23) 鏇存柊缁熻瀛楁
-- ============================================================
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

SELECT '鏁版嵁搴撳垵濮嬪寲瀹屾垚锟? AS message;


