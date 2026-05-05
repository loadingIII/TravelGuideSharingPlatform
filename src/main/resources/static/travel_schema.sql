SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS travel_guide_platform
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE travel_guide_platform;

-- 1) 用户
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

-- 2) 目的地
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

CREATE TABLE IF NOT EXISTS destination_showcases (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  destination_id BIGINT UNSIGNED NULL,
  section ENUM('recommended','popular','inspiration') NOT NULL,
  display_title VARCHAR(100) NOT NULL,
  display_subtitle VARCHAR(255) NULL,
  display_description VARCHAR(1000) NULL,
  display_image_url VARCHAR(500) NULL,
  display_rating DECIMAL(3,2) NULL,
  sort_order INT UNSIGNED NOT NULL DEFAULT 0,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_showcases_section_order (section, sort_order),
  KEY idx_showcases_destination (destination_id),
  CONSTRAINT fk_showcases_destination
    FOREIGN KEY (destination_id) REFERENCES destinations(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3) 攻略主表
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

-- 4) 标签
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

-- 5) 行程结构（天 -> 景点）
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

CREATE TABLE IF NOT EXISTS guide_itinerary_spots (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  itinerary_day_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(150) NOT NULL,
  description VARCHAR(1000) NULL,
  visit_time TIME NULL,
  duration_minutes SMALLINT UNSIGNED NULL,
  image_url VARCHAR(500) NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_itinerary_spots_day (itinerary_day_id, sort_order),
  CONSTRAINT fk_itinerary_spots_day
    FOREIGN KEY (itinerary_day_id) REFERENCES guide_itinerary_days(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6) 实用信息 tips
CREATE TABLE IF NOT EXISTS guide_tips (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  category_name VARCHAR(100) NOT NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_guide_tip_category (guide_id, category_name),
  KEY idx_guide_tips_guide (guide_id),
  CONSTRAINT fk_guide_tips_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS guide_tip_items (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  tip_id BIGINT UNSIGNED NOT NULL,
  item_text VARCHAR(300) NOT NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_guide_tip_items_tip (tip_id, sort_order),
  CONSTRAINT fk_guide_tip_items_tip
    FOREIGN KEY (tip_id) REFERENCES guide_tips(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7) 预算拆分
CREATE TABLE IF NOT EXISTS guide_budget_items (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  category_code VARCHAR(50) NOT NULL,
  category_name VARCHAR(50) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  percentage DECIMAL(5,2) NOT NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_guide_budget_item (guide_id, category_code),
  KEY idx_guide_budget_guide (guide_id),
  CONSTRAINT fk_guide_budget_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 8) 相关攻略
CREATE TABLE IF NOT EXISTS guide_related (
  guide_id BIGINT UNSIGNED NOT NULL,
  related_guide_id BIGINT UNSIGNED NOT NULL,
  sort_order SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (guide_id, related_guide_id),
  KEY idx_guide_related_related (related_guide_id),
  CONSTRAINT fk_guide_related_guide
    FOREIGN KEY (guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_guide_related_related
    FOREIGN KEY (related_guide_id) REFERENCES guides(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 9) 攻略评论
CREATE TABLE IF NOT EXISTS guide_comments (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  guide_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  parent_comment_id BIGINT UNSIGNED NULL,
  author_name VARCHAR(50) NOT NULL,
  author_avatar_url VARCHAR(500) NULL,
  content TEXT NOT NULL,
  likes_count INT UNSIGNED NOT NULL DEFAULT 0,
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

-- 10) 攻略互动：点赞/收藏/评论点赞
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

-- 11) 用户关注（作者关注）
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

-- 12) 旅行者故事（社区流）
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

