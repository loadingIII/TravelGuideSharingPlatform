SET NAMES utf8mb4;

USE travel_guide_platform;

-- ============================================================
-- 1) 用户数据
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
-- 2) 用户档案
-- ============================================================
INSERT INTO user_profiles (user_id, nickname, avatar_url, bio, is_vip, guides_count, followers_count, likes_received_count) VALUES
(1, '旅行达人小明', '/avatars/user1.jpg', '热爱探索世界各地的文化与美食，已完成30+国家旅行', 1, 5, 1250, 3420),
(2, '背包客小红', '/avatars/user2.jpg', '穷游爱好者，用最少的钱走最多的路', 0, 3, 890, 2150),
(3, '探险家阿强', '/avatars/user3.jpg', '极限运动爱好者，挑战世界各地的险峻山峰', 1, 4, 2100, 5680),
(4, '摄影师小李', '/avatars/user4.jpg', '用镜头记录旅途中的美好瞬间', 0, 6, 1560, 4320),
(5, '美食家小王', '/avatars/user5.jpg', '走遍世界只为寻找最地道的美食', 1, 2, 780, 1890),
(6, '穷游背包客', '/avatars/user6.jpg', '学生党穷游攻略分享，月均旅行预算2000元', 0, 4, 2340, 6780),
(7, '奢华旅行家', '/avatars/user7.jpg', '分享高端酒店和私人定制旅行体验', 1, 3, 1890, 4560),
(8, '独自旅行者', '/avatars/user8.jpg', '一个人的旅行，一个人的精彩', 0, 5, 1230, 3210),
(9, '吃货旅行家', '/avatars/user9.jpg', '为了美食可以飞越半个地球', 1, 2, 670, 1540),
(10, '旅行摄影师', '/avatars/user10.jpg', '专业旅行摄影，记录世界的美', 0, 7, 3450, 8920),
(11, '历史文化迷', '/avatars/user11.jpg', '喜欢探访历史古迹，了解各地文化', 0, 3, 980, 2670),
(12, '自然探索者', '/avatars/user12.jpg', '热爱大自然，徒步登山是我的生活方式', 1, 4, 1560, 4230);

-- ============================================================
-- 3) 目的地数据
-- ============================================================
INSERT INTO destinations (id, name, country, city, description, cover_image_url, rating_avg, guides_count, travelers_count, popularity_score) VALUES
(1, '东京', '日本', '东京', '日本首都，融合传统与现代的国际大都市，拥有丰富的美食文化和购物体验', '/destinations/tokyo.jpg', 4.85, 15, 2560, 95.50),
(2, '巴黎', '法国', '巴黎', '浪漫之都，艺术与时尚的中心，埃菲尔铁塔和卢浮宫的所在地', '/destinations/paris.jpg', 4.90, 12, 2340, 98.20),
(3, '曼谷', '泰国', '曼谷', '东南亚最受欢迎的旅游城市，以寺庙、美食和夜市闻名', '/destinations/bangkok.jpg', 4.75, 10, 1890, 88.70),
(4, '纽约', '美国', '纽约', '世界金融中心，拥有自由女神像、时代广场等标志性景点', '/destinations/newyork.jpg', 4.80, 8, 2100, 92.30),
(5, '巴厘岛', '印度尼西亚', '巴厘岛', '印尼著名度假胜地，以海滩、梯田和独特的宗教文化著称', '/destinations/bali.jpg', 4.70, 9, 1670, 85.60),
(6, '伦敦', '英国', '伦敦', '历史悠久的国际都市，大本钟、伦敦眼和白金汉宫必游', '/destinations/london.jpg', 4.75, 7, 1890, 89.40),
(7, '北京', '中国', '北京', '中国首都，拥有故宫、长城等世界文化遗产', '/destinations/beijing.jpg', 4.85, 14, 3200, 96.80),
(8, '上海', '中国', '上海', '中国最大城市，现代与传统交融的国际金融中心', '/destinations/shanghai.jpg', 4.80, 11, 2890, 93.50),
(9, '新加坡', '新加坡', '新加坡', '花园城市，以干净整洁和美食闻名，适合家庭旅游', '/destinations/singapore.jpg', 4.75, 6, 1450, 82.30),
(10, '清迈', '泰国', '清迈', '泰北玫瑰，以古城寺庙、夜市和悠闲氛围著称', '/destinations/chiangmai.jpg', 4.65, 5, 1230, 78.90),
(11, '京都', '日本', '京都', '日本古都，保存完好的寺庙神社和传统日式庭院', '/destinations/kyoto.jpg', 4.80, 8, 1670, 87.50),
(12, '马尔代夫', '马尔代夫', '马累', '印度洋上的珍珠，以水上别墅和清澈海水闻名', '/destinations/maldives.jpg', 4.90, 4, 980, 91.20);

-- ============================================================
-- 4) 目的地展示
-- ============================================================
INSERT INTO destination_showcases (destination_id, section, display_title, display_subtitle, display_description, display_image_url, display_rating, sort_order, is_active) VALUES
(1, 'recommended', '东京深度游', '探索日本首都的无限魅力', '从传统神社到现代都市，体验东京的独特韵味', '/showcases/tokyo_deep.jpg', 4.90, 1, 1),
(2, 'recommended', '浪漫巴黎之旅', '感受世界浪漫之都', '漫步塞纳河畔，品味法式优雅生活', '/showcases/paris_romantic.jpg', 4.95, 2, 1),
(3, 'popular', '曼谷美食之旅', '舌尖上的泰国', '从街头小吃到高级餐厅，品味地道泰式美食', '/showcases/bangkok_food.jpg', 4.80, 1, 1),
(7, 'inspiration', '北京文化探索', '穿越千年历史长河', '探访故宫、长城，感受中华文明的博大精深', '/showcases/beijing_culture.jpg', 4.85, 1, 1),
(8, 'inspiration', '上海现代都市', '东方明珠的璀璨', '体验上海的现代与传统，感受魔都的独特魅力', '/showcases/shanghai_modern.jpg', 4.80, 2, 1),
(12, 'recommended', '马尔代夫度假天堂', '印度洋上的梦幻天堂', '水上别墅、清澈海水，享受极致的海岛度假体验', '/showcases/maldives_paradise.jpg', 4.95, 3, 1);

-- ============================================================
-- 5) 标签数据
-- ============================================================
INSERT INTO tags (id, name) VALUES
(1, '美食'),
(2, '购物'),
(3, '文化'),
(4, '自然'),
(5, '冒险'),
(6, '浪漫'),
(7, '家庭'),
(8, '摄影'),
(9, '历史'),
(10, '现代'),
(11, '海滩'),
(12, '城市'),
(13, '乡村'),
(14, '登山'),
(15, '潜水');

-- ============================================================
-- 6) 攻略主表数据
-- ============================================================
INSERT INTO guides (id, destination_id, author_id, title, summary, content_html, cover_image_url, location_text, scope, travel_mode, publish_status, published_at, days, budget_total, views_count, likes_count, comments_count, favorites_count) VALUES
(1, 1, 1, '东京7日深度游：从浅草寺到涩谷的完美行程', '带你领略东京的传统与现代魅力，包含详细行程安排和美食推荐', '<h1>东京7日深度游</h1><p>东京是一个充满活力的城市...</p>', '/guides/tokyo_guide1.jpg', '东京', 'international', 'free', 'published', '2026-04-15 10:00:00', 7, 15000.00, 12500, 450, 89, 230),
(2, 2, 4, '巴黎5日浪漫之旅：艺术与美食的完美结合', '探索巴黎的艺术殿堂，品味正宗法式美食', '<h1>巴黎浪漫之旅</h1><p>巴黎是艺术与时尚的中心...</p>', '/guides/paris_guide1.jpg', '巴黎', 'international', 'honeymoon', 'published', '2026-04-10 14:30:00', 5, 18000.00, 9800, 380, 76, 195),
(3, 3, 2, '曼谷6日穷游攻略：人均3000玩转泰国首都', '低预算也能玩转曼谷，省钱又有趣的旅行指南', '<h1>曼谷穷游攻略</h1><p>曼谷是东南亚最受欢迎的旅游城市...</p>', '/guides/bangkok_guide1.jpg', '曼谷', 'international', 'free', 'published', '2026-04-05 09:15:00', 6, 3000.00, 15600, 620, 134, 310),
(4, 7, 3, '北京5日文化之旅：探寻中华文明的瑰宝', '深度体验北京的历史文化，从故宫到长城的完整攻略', '<h1>北京文化之旅</h1><p>北京是中国的首都...</p>', '/guides/beijing_guide1.jpg', '北京', 'domestic', 'family', 'published', '2026-03-28 11:45:00', 5, 8000.00, 18900, 720, 156, 420),
(5, 8, 10, '上海3日现代都市游：感受魔都的独特魅力', '体验上海的现代与传统，从外滩到田子坊', '<h1>上海现代都市游</h1><p>上海是中国最大的城市...</p>', '/guides/shanghai_guide1.jpg', '上海', 'domestic', 'free', 'published', '2026-03-20 16:20:00', 3, 5000.00, 11200, 430, 98, 265),
(6, 5, 5, '巴厘岛7日蜜月之旅：浪漫与自然的完美融合', '适合新婚夫妇的巴厘岛浪漫行程', '<h1>巴厘岛蜜月之旅</h1><p>巴厘岛是印尼著名的度假胜地...</p>', '/guides/bali_guide1.jpg', '巴厘岛', 'international', 'honeymoon', 'published', '2026-03-15 13:30:00', 7, 25000.00, 8900, 340, 72, 180),
(7, 1, 8, '东京3日快速游：首次访问东京的必看指南', '适合时间有限的东京精华游', '<h1>东京快速游</h1><p>只有3天时间也能玩转东京...</p>', '/guides/tokyo_guide2.jpg', '东京', 'international', 'free', 'published', '2026-03-10 10:00:00', 3, 6000.00, 7800, 290, 54, 145),
(8, 4, 6, '纽约4日经济游：学生党的纽约梦', '低预算玩转纽约，省钱又有趣的攻略', '<h1>纽约经济游</h1><p>纽约是世界金融中心...</p>', '/guides/newyork_guide1.jpg', '纽约', 'international', 'free', 'published', '2026-03-05 15:45:00', 4, 8000.00, 6500, 250, 48, 120),
(9, 11, 11, '京都4日文化游：穿越千年的时光之旅', '深度体验京都的传统文化和历史', '<h1>京都文化游</h1><p>京都是日本的古都...</p>', '/guides/kyoto_guide1.jpg', '京都', 'international', 'free', 'published', '2026-02-28 09:30:00', 4, 10000.00, 5600, 220, 42, 95),
(10, 12, 7, '马尔代夫5日奢华游：水上别墅的极致体验', '享受顶级度假体验，水上别墅和私人沙滩', '<h1>马尔代夫奢华游</h1><p>马尔代夫是印度洋上的珍珠...</p>', '/guides/maldives_guide1.jpg', '马累', 'international', 'honeymoon', 'published', '2026-02-20 14:00:00', 5, 50000.00, 12000, 480, 92, 280);

-- ============================================================
-- 7) 攻略标签关联
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
-- 8) 攻略行程天数
-- ============================================================
INSERT INTO guide_itinerary_days (guide_id, day_no, title, summary, sort_order) VALUES
(1, 1, '抵达东京 & 浅草寺', '抵达东京后前往浅草寺，感受传统日式文化', 1),
(1, 2, '涩谷 & 原宿潮流之旅', '探索东京最时尚的街区', 2),
(1, 3, '东京迪士尼乐园', '在迪士尼乐园度过欢乐的一天', 3),
(1, 4, '秋叶原 & 上野', '动漫圣地秋叶原和文化气息浓厚的上野', 4),
(1, 5, '新宿 & 歌舞伎町', '体验东京的夜生活', 5),
(1, 6, '箱根一日游', '前往箱根欣赏富士山美景', 6),
(1, 7, '返回 & 购物', '最后一天购物和整理行李', 7),
(4, 1, '天安门广场 & 故宫', '游览世界最大的城市广场和明清皇宫', 1),
(4, 2, '长城一日游', '登上万里长城，感受古代工程奇迹', 2),
(4, 3, '颐和园 & 圆明园', '欣赏皇家园林的精美', 3),
(4, 4, '南锣鼓巷 & 什刹海', '体验老北京的胡同文化', 4),
(4, 5, '798艺术区 & 三里屯', '感受北京的现代艺术和时尚', 5),
(3, 1, '大皇宫 & 卧佛寺', '参观泰国最著名的佛教建筑', 1),
(3, 2, '暹罗商圈购物', '在曼谷最大的购物中心购物', 2),
(3, 3, '水上市场 & 铁道市场', '体验泰国独特的水上市场文化', 3),
(3, 4, '考山路夜生活', '在考山路感受曼谷的夜生活', 4),
(3, 5, '美功铁道市场', '观看火车穿过市场的奇观', 5),
(3, 6, '返回 & 最后购物', '购买纪念品和特产', 6);

-- ============================================================
-- 9) 攻略行程景点
-- ============================================================
INSERT INTO guide_itinerary_spots (itinerary_day_id, name, description, visit_time, duration_minutes, image_url, sort_order) VALUES
(1, '浅草寺', '东京最古老的寺庙，雷门是标志性建筑', '09:00:00', 120, '/spots/sensoji.jpg', 1),
(1, '仲见世通', '浅草寺前的繁华商店街', '11:00:00', 60, '/spots/nakamise.jpg', 2),
(2, '涩谷十字路口', '世界最繁忙的十字路口', '10:00:00', 30, '/spots/shibuya.jpg', 1),
(2, '原宿竹下通', '年轻人的时尚圣地', '11:00:00', 120, '/spots/harajuku.jpg', 2),
(8, '天安门广场', '世界最大的城市广场', '08:00:00', 90, '/spots/tiananmen.jpg', 1),
(8, '故宫博物院', '明清两代的皇宫', '09:30:00', 240, '/spots/forbidden_city.jpg', 2),
(9, '八达岭长城', '长城最著名的段落', '07:00:00', 360, '/spots/great_wall.jpg', 1),
(13, '大皇宫', '泰国最著名的佛教建筑群', '09:00:00', 180, '/spots/grand_palace.jpg', 1),
(14, '暹罗中心', '曼谷最大的购物中心', '10:00:00', 240, '/spots/siam_center.jpg', 1);

-- ============================================================
-- 10) 攻略提示
-- ============================================================
INSERT INTO guide_tips (guide_id, category_name, sort_order) VALUES
(1, '交通指南', 1),
(1, '美食推荐', 2),
(1, '住宿建议', 3),
(1, '注意事项', 4),
(4, '交通指南', 1),
(4, '门票信息', 2),
(4, '美食推荐', 3),
(3, '省钱技巧', 1),
(3, '美食推荐', 2),
(3, '交通指南', 3);

-- ============================================================
-- 11) 攻略提示项目
-- ============================================================
INSERT INTO guide_tip_items (tip_id, item_text, sort_order) VALUES
(1, '建议购买东京地铁72小时通票，约1500日元', 1),
(1, '从成田机场到市区可乘坐Skyliner约36分钟', 2),
(1, '市区内推荐使用Suica卡，方便乘坐地铁和便利店消费', 3),
(2, '必吃：一兰拉面、筑地市场海鲜、章鱼小丸子', 1),
(2, '推荐餐厅：蟹道乐、CoCo壹番屋、松屋', 2),
(3, '推荐住在新宿或涩谷，交通便利', 1),
(3, '预算充足可选择东京塔附近的酒店', 2),
(4, '日本靠左行驶，扶梯也靠左站', 1),
(4, '寺庙内禁止拍照，请注意标识', 2),
(4, '地铁内请勿打电话，保持安静', 3),
(5, '建议办理北京一卡通，乘坐地铁公交更方便', 1),
(5, '可下载"亿通行"APP扫码乘车', 2),
(6, '故宫门票需提前在网上预约，旺季很抢手', 1),
(6, '长城门票40元，建议网上购买', 2),
(7, '必吃：北京烤鸭、炸酱面、豆汁儿', 1),
(8, '在曼谷坐BTS最划算，避免堵车', 1),
(8, '打车请要求打表，或使用Grab叫车', 2),
(9, '必吃：芒果糯米饭、冬阴功汤、泰式奶茶', 1),
(10, 'BTS一日通票150泰铢，可无限次乘坐', 1);

-- ============================================================
-- 12) 攻略预算项目
-- ============================================================
INSERT INTO guide_budget_items (guide_id, category_code, category_name, amount, percentage, sort_order) VALUES
(1, 'flights', '机票', 5000.00, 33.33, 1),
(1, 'accommodation', '住宿', 4500.00, 30.00, 2),
(1, 'food', '餐饮', 2500.00, 16.67, 3),
(1, 'transport', '交通', 1500.00, 10.00, 4),
(1, 'shopping', '购物', 1000.00, 6.67, 5),
(1, 'tickets', '门票', 500.00, 3.33, 6),
(2, 'flights', '机票', 8000.00, 44.44, 1),
(2, 'accommodation', '住宿', 5000.00, 27.78, 2),
(2, 'food', '餐饮', 3000.00, 16.67, 3),
(2, 'transport', '交通', 1000.00, 5.56, 4),
(2, 'shopping', '购物', 1000.00, 5.56, 5),
(3, 'flights', '机票', 1000.00, 33.33, 1),
(3, 'accommodation', '住宿', 800.00, 26.67, 2),
(3, 'food', '餐饮', 600.00, 20.00, 3),
(3, 'transport', '交通', 300.00, 10.00, 4),
(3, 'shopping', '购物', 200.00, 6.67, 5),
(3, 'tickets', '门票', 100.00, 3.33, 6),
(4, 'flights', '机票', 2000.00, 25.00, 1),
(4, 'accommodation', '住宿', 2500.00, 31.25, 2),
(4, 'food', '餐饮', 1500.00, 18.75, 3),
(4, 'transport', '交通', 800.00, 10.00, 4),
(4, 'shopping', '购物', 700.00, 8.75, 5),
(4, 'tickets', '门票', 500.00, 6.25, 6);

-- ============================================================
-- 13) 相关攻略
-- ============================================================
INSERT INTO guide_related (guide_id, related_guide_id, sort_order) VALUES
(1, 7, 1),
(1, 9, 2),
(2, 6, 1),
(2, 10, 2),
(3, 9, 1),
(4, 5, 1),
(5, 4, 1),
(6, 10, 1),
(7, 1, 1),
(7, 9, 2);

-- ============================================================
-- 14) 攻略评论
-- ============================================================
INSERT INTO guide_comments (guide_id, user_id, parent_comment_id, author_name, author_avatar_url, content, likes_count) VALUES
(1, 2, NULL, '背包客小红', '/avatars/user2.jpg', '太详细了！正好计划去东京，收藏了！', 25),
(1, 3, 1, '探险家阿强', '/avatars/user3.jpg', '同求东京攻略，请问迪士尼值得去吗？', 8),
(1, 1, 2, '旅行达人小明', '/avatars/user1.jpg', '迪士尼非常值得去，建议安排一整天', 15),
(2, 5, NULL, '美食家小王', '/avatars/user5.jpg', '巴黎的餐厅推荐太棒了，下次去一定要试试', 18),
(2, 7, NULL, '奢华旅行家', '/avatars/user7.jpg', '这个预算对于蜜月游来说很合理', 12),
(3, 6, NULL, '穷游背包客', '/avatars/user6.jpg', '穷游攻略太实用了，感谢分享！', 32),
(3, 8, 6, '独自旅行者', '/avatars/user8.jpg', '请问曼谷安全吗？一个人去需要注意什么？', 5),
(4, 11, NULL, '历史文化迷', '/avatars/user11.jpg', '北京的文化之旅写得很详细，收藏了', 28),
(4, 12, NULL, '自然探索者', '/avatars/user12.jpg', '长城的描述很真实，确实很壮观', 22),
(5, 1, NULL, '旅行达人小明', '/avatars/user1.jpg', '上海的现代都市感确实很强', 15);

-- ============================================================
-- 15) 攻略点赞
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
-- 16) 攻略收藏
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
-- 17) 攻略评论点赞
-- ============================================================
INSERT INTO guide_comment_likes (comment_id, user_id) VALUES
(1, 3), (1, 4), (1, 5),
(2, 1), (2, 4),
(3, 2), (3, 5),
(4, 1), (4, 6),
(5, 1), (5, 3),
(6, 1), (6, 4), (6, 8),
(7, 3),
(8, 1), (8, 12),
(9, 1), (9, 11),
(10, 3), (10, 6);

-- ============================================================
-- 18) 用户关注
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
-- 19) 旅行者故事
-- ============================================================
INSERT INTO traveler_stories (author_user_id, author_name, author_avatar_url, is_vip, content, published_at, likes_count, comments_count, shares_count) VALUES
(1, '旅行达人小明', '/avatars/user1.jpg', 1, '刚刚从东京回来，太美了！浅草寺的樱花季真的不容错过。这次尝试了当地的小巷美食，发现了很多隐藏的宝藏餐厅。', '2026-04-28 18:30:00', 156, 23, 12),
(3, '探险家阿强', '/avatars/user3.jpg', 1, '今天挑战了北京慕田峪长城，虽然很累但成就感满满！站在长城上俯瞰群山，感觉一切都值得了。', '2026-04-27 20:15:00', 234, 45, 28),
(5, '美食家小王', '/avatars/user5.jpg', 1, '曼谷的街头美食真的太棒了！从芒果糯米饭到泰式奶茶，每一口都是惊喜。强烈推荐大家去考山路逛逛。', '2026-04-26 16:45:00', 189, 34, 15),
(10, '旅行摄影师', '/avatars/user10.jpg', 0, '在巴黎埃菲尔铁塔下拍到了最美的日落，这次的摄影作品太满意了。分享几张精选照片给大家。', '2026-04-25 21:00:00', 312, 56, 42),
(8, '独自旅行者', '/avatars/user8.jpg', 0, '一个人的旅行也能很精彩！在清迈古城骑行，感受当地人的慢生活，这种自由的感觉太棒了。', '2026-04-24 19:30:00', 145, 28, 18),
(6, '穷游背包客', '/avatars/user6.jpg', 0, '学生党也能玩转纽约！分享我的穷游攻略，5天只花了5000元，包含机票住宿和所有开销。', '2026-04-23 17:20:00', 278, 67, 35),
(7, '奢华旅行家', '/avatars/user7.jpg', 1, '马尔代夫的水上别墅真的太梦幻了！从阳台直接跳进清澈的海里，这种体验无与伦比。', '2026-04-22 15:10:00', 423, 78, 56),
(12, '自然探索者', '/avatars/user12.jpg', 1, '在巴厘岛徒步了阿贡火山，虽然很艰辛，但山顶的日出美到窒息。大自然的力量真的很震撼。', '2026-04-21 13:45:00', 198, 32, 22);

-- ============================================================
-- 20) 旅行者故事图片
-- ============================================================
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
-- 21) 旅行者故事点赞
-- ============================================================
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
-- 22) 旅行者故事评论
-- ============================================================
INSERT INTO traveler_story_comments (story_id, user_id, parent_comment_id, author_name, author_avatar_url, content, likes_count) VALUES
(1, 2, NULL, '背包客小红', '/avatars/user2.jpg', '樱花季真的太美了！请问是几月份去的？', 8),
(1, 1, 1, '旅行达人小明', '/avatars/user1.jpg', '我是3月底4月初去的，正好赶上樱花盛开', 5),
(2, 12, NULL, '自然探索者', '/avatars/user12.jpg', '长城真的很壮观，下次一定要去！', 12),
(3, 9, NULL, '吃货旅行家', '/avatars/user9.jpg', '曼谷的美食真的太诱人了，已经加入旅行清单', 15),
(4, 3, NULL, '探险家阿强', '/avatars/user3.jpg', '这些照片拍得太美了，摄影技术真棒！', 20),
(4, 10, 5, '旅行摄影师', '/avatars/user10.jpg', '谢谢夸奖！那天的光线特别好', 8),
(5, 6, NULL, '穷游背包客', '/avatars/user6.jpg', '一个人的旅行也很精彩，学到了很多', 10),
(6, 3, NULL, '探险家阿强', '/avatars/user3.jpg', '5000元玩纽约太厉害了，请问是怎么做到的？', 18),
(6, 6, 8, '穷游背包客', '/avatars/user6.jpg', '主要是住青旅和吃街头美食，交通用地铁通票', 12),
(7, 1, NULL, '旅行达人小明', '/avatars/user1.jpg', '水上别墅太梦幻了，蜜月首选！', 25),
(8, 3, NULL, '探险家阿强', '/avatars/user3.jpg', '阿贡火山徒步确实很挑战，但值得！', 15);

-- ============================================================
-- 更新统计字段（可选）
-- ============================================================
-- 注意：在实际应用中，这些统计字段通常由触发器或应用层维护
-- 这里为了数据完整性，手动更新一下

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

SELECT '模拟数据插入完成！' AS message;