-- 管理员审核管理：添加 status 字段
-- 执行前请备份数据库

-- 用户表：修改 status 字段注释（值 0=待审核, 1=正常, 2=禁用）
ALTER TABLE users MODIFY COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=正常, 2=禁用';

-- 攻略表：新增 status 字段
ALTER TABLE guides ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架' AFTER favorites_count;

-- 故事表：新增 status 字段
ALTER TABLE traveler_stories ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架' AFTER shares_count;

-- 评论表：新增 status 字段
ALTER TABLE guide_comments ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架' AFTER likes_count;
