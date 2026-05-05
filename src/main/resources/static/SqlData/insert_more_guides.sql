USE travel_guide_platform;

-- 插入更多攻略数据（基于新添加的目的地）
-- 使用变量获取插入后的ID
SET @guide_start_id = (SELECT IFNULL(MAX(id), 0) FROM guides);

INSERT INTO guides (destination_id, author_id, title, summary, cover_image_url, location_text, scope, travel_mode, publish_status, published_at, days, budget_total, views_count, likes_count) VALUES
-- 伦敦攻略 (destination_id: 15)
(15, 10, '伦敦三日文化之旅', '大本钟、伦敦塔桥、大英博物馆，一天一个主题深度体验英伦文化，地铁是最方便的交通方式...', '/img/埃菲尔铁塔.jpg', '英国·伦敦', 'international', 'free', 'published', NOW(), 3, 12000.00, 9850, 1876),
(15, 11, '伦敦周末购物攻略', '牛津街、摄政街、哈罗德百货，从平价到奢侈品一站式购物体验，记得办理退税...', '/img/埃菲尔铁塔.jpg', '英国·伦敦', 'international', 'free', 'published', NOW(), 2, 15000.00, 7650, 1423),

-- 阿姆斯特丹攻略 (destination_id: 16)
(16, 1, '阿姆斯特丹运河漫步', '租一辆自行车穿梭于运河之间，安妮之家与梵高博物馆是必去之地，红灯区了解一下...', '/img/希腊圣托尼尼.png', '荷兰·阿姆斯特丹', 'international', 'free', 'published', NOW(), 3, 9000.00, 8920, 1654),
(16, 2, '荷兰郁金香季攻略', '4月是郁金香盛开的季节，库肯霍夫花园美不胜收，建议提前网上订票避免排队...', '/img/希腊圣托尼尼.png', '荷兰·阿姆斯特丹', 'international', 'free', 'published', NOW(), 5, 11000.00, 11200, 2100),

-- 布拉格攻略 (destination_id: 17)
(17, 3, '布拉格中世纪之旅', '查理大桥的日出最美，老城广场的天文钟整点报时，城堡区可以俯瞰整个城市...', '/img/埃菲尔铁塔.jpg', '捷克·布拉格', 'international', 'free', 'published', NOW(), 2, 5000.00, 12300, 2340),
(17, 4, '布拉格美食与啤酒', '捷克的啤酒世界闻名，猪肘配啤酒是经典搭配，甜点推荐Trdelník...', '/img/埃菲尔铁塔.jpg', '捷克·布拉格', 'international', 'free', 'published', NOW(), 3, 6000.00, 8760, 1650),

-- 维也纳攻略 (destination_id: 18)
(18, 5, '维也纳音乐之旅', '金色大厅听一场音乐会，美泉宫感受茜茜公主的生活，中央咖啡馆品尝萨赫蛋糕...', '/img/埃菲尔铁塔.jpg', '奥地利·维也纳', 'international', 'free', 'published', NOW(), 3, 10000.00, 9450, 1780),
(18, 6, '维也纳博物馆攻略', '艺术史博物馆、分离派展览馆、阿尔贝蒂娜博物馆，一天逛不完的艺术殿堂...', '/img/埃菲尔铁塔.jpg', '奥地利·维也纳', 'international', 'free', 'published', NOW(), 4, 8000.00, 7890, 1456),

-- 苏黎世攻略 (destination_id: 19)
(19, 7, '苏黎世湖畔漫步', '班霍夫大街购物，苏黎世湖游船，老城区的双塔教堂是地标...', '/img/希腊圣托尼尼.png', '瑞士·苏黎世', 'international', 'free', 'published', NOW(), 2, 15000.00, 6780, 1234),

-- 莫斯科攻略 (destination_id: 20)
(20, 8, '莫斯科红场深度游', '克里姆林宫、圣瓦西里大教堂、古姆百货，感受俄罗斯的庄严与浪漫...', '/img/埃菲尔铁塔.jpg', '俄罗斯·莫斯科', 'international', 'free', 'published', NOW(), 3, 8000.00, 8900, 1670),

-- 马尔代夫攻略 (destination_id: 21)
(21, 9, '马尔代夫蜜月天堂', '水上别墅、浮潜看魔鬼鱼、日落巡航，这才是度假的正确打开方式...', '/img/巴厘岛.jpeg', '马尔代夫·马累', 'international', 'honeymoon', 'published', NOW(), 5, 35000.00, 18900, 4560),
(21, 10, '马尔代夫选岛攻略', '不同岛屿适合不同人群，预算从一万到十万都有选择，这篇帮你选对岛...', '/img/巴厘岛.jpeg', '马尔代夫·马累', 'international', 'honeymoon', 'published', NOW(), 7, 30000.00, 15600, 3210),

-- 河内攻略 (destination_id: 22)
(22, 11, '越南河内美食之旅', '河粉、春卷、滴漏咖啡，老城区的街头美食让你吃到扶墙走...', '/img/长尾船.png', '越南·河内', 'international', 'free', 'published', NOW(), 4, 3000.00, 11200, 2100),
(22, 12, '河内到下龙湾一日游', '世界自然遗产下龙湾，乘船游览千岛湖般的喀斯特地貌...', '/img/长尾船.png', '越南·河内', 'international', 'free', 'published', NOW(), 1, 2000.00, 8900, 1680),

-- 曼谷攻略 (destination_id: 23)
(23, 1, '曼谷寺庙巡礼', '大皇宫、卧佛寺、郑王庙，金碧辉煌的泰国佛教文化...', '/img/长尾船.png', '泰国·曼谷', 'international', 'free', 'published', NOW(), 3, 4000.00, 13400, 2560),
(23, 2, '曼谷夜市全攻略', '拉差达火车夜市、考山路、暹罗广场，夜市文化一网打尽...', '/img/长尾船.png', '泰国·曼谷', 'international', 'free', 'published', NOW(), 5, 5000.00, 15600, 2890),

-- 吉隆坡攻略 (destination_id: 24)
(24, 3, '吉隆坡双子塔打卡', '双子塔登顶看夜景，阿罗街美食街，黑风洞探秘...', '/img/新加坡.jpg', '马来西亚·吉隆坡', 'international', 'free', 'published', NOW(), 3, 5000.00, 8700, 1560),

-- 香港攻略 (destination_id: 25)
(25, 4, '香港三日购物游', '铜锣湾、尖沙咀、旺角，从奢侈品到街边小店一站式购物...', '/img/新加坡.jpg', '中国·香港', 'domestic', 'free', 'published', NOW(), 3, 10000.00, 16800, 3210),
(25, 5, '香港迪士尼乐园攻略', '一天玩遍所有热门项目，快速通行证使用技巧，烟花秀最佳观赏位置...', '/img/新加坡.jpg', '中国·香港', 'domestic', 'family', 'published', NOW(), 2, 8000.00, 12300, 2340),

-- 澳门攻略 (destination_id: 26)
(26, 6, '澳门一日游攻略', '大三巴、威尼斯人、新濠天地，感受中西文化交融的博彩之城...', '/img/新加坡.jpg', '中国·澳门', 'domestic', 'free', 'published', NOW(), 1, 3000.00, 9800, 1780),

-- 台北攻略 (destination_id: 27)
(27, 7, '台北夜市美食之旅', '士林夜市、饶河夜市、宁夏夜市，从卤肉饭到鸡排，夜市美食全攻略...', '/img/新加坡.jpg', '中国·台北', 'domestic', 'free', 'published', NOW(), 4, 5000.00, 14500, 2670),
(27, 8, '台北文艺之旅', '诚品书店、台北故宫、九份老街，感受宝岛的文化底蕴...', '/img/新加坡.jpg', '中国·台北', 'domestic', 'free', 'published', NOW(), 3, 4000.00, 11200, 2010),

-- 洛杉矶攻略 (destination_id: 28)
(28, 9, '洛杉矶好莱坞之旅', '好莱坞星光大道、环球影城、圣莫尼卡海滩，感受美式文化...', '/img/纽约城市.jpg', '美国·洛杉矶', 'international', 'free', 'published', NOW(), 5, 15000.00, 13400, 2560),
(28, 10, '洛杉矶购物攻略', '奥特莱斯、罗迪欧大道、格罗夫购物中心，从平价到奢侈品...', '/img/纽约城市.jpg', '美国·洛杉矶', 'international', 'free', 'published', NOW(), 3, 20000.00, 9800, 1780),

-- 旧金山攻略 (destination_id: 29)
(29, 11, '旧金山金门大桥之旅', '金门大桥骑行、渔人码头品尝海鲜、恶魔岛探秘...', '/img/纽约城市.jpg', '美国·旧金山', 'international', 'free', 'published', NOW(), 3, 12000.00, 11200, 2100),

-- 温哥华攻略 (destination_id: 30)
(30, 12, '温哥华自然之旅', '斯坦利公园、卡皮拉诺吊桥、惠斯勒滑雪场，感受加拿大的自然之美...', '/img/纽约城市.jpg', '加拿大·温哥华', 'international', 'free', 'published', NOW(), 4, 10000.00, 8900, 1680),

-- 多伦多攻略 (destination_id: 31)
(31, 1, '多伦多城市探索', 'CN塔、安大略湖、多伦多群岛，北美多元文化之都...', '/img/纽约城市.jpg', '加拿大·多伦多', 'international', 'free', 'published', NOW(), 3, 8000.00, 7600, 1423),

-- 迈阿密攻略 (destination_id: 32)
(32, 2, '迈阿密海滩度假', '南海滩、大沼泽地国家公园、小哈瓦那，感受拉丁风情...', '/img/纽约城市.jpg', '美国·迈阿密', 'international', 'free', 'published', NOW(), 4, 12000.00, 9400, 1750),

-- 墨尔本攻略 (destination_id: 33)
(33, 3, '墨尔本咖啡文化之旅', '弗林德斯街车站、联邦广场、涂鸦小巷，感受咖啡之都的魅力...', '/img/悉尼歌剧院.jpg', '澳大利亚·墨尔本', 'international', 'free', 'published', NOW(), 3, 10000.00, 12300, 2340),
(33, 4, '墨尔本大洋路自驾', '十二门徒石、大洋路、洛恩小镇，澳洲最美的海岸线...', '/img/悉尼歌剧院.jpg', '澳大利亚·墨尔本', 'international', 'free', 'published', NOW(), 2, 8000.00, 15600, 2890),

-- 奥克兰攻略 (destination_id: 34)
(34, 5, '奥克兰帆船体验', '天空塔、维多利亚港、怀赫科岛，帆船之都的正确打开方式...', '/img/悉尼歌剧院.jpg', '新西兰·奥克兰', 'international', 'free', 'published', NOW(), 3, 9000.00, 8700, 1560),

-- 皇后镇攻略 (destination_id: 35)
(35, 6, '皇后镇极限挑战', '蹦极、跳伞、喷射快艇，冒险之都的肾上腺素之旅...', '/img/悉尼歌剧院.jpg', '新西兰·皇后镇', 'international', 'free', 'published', NOW(), 4, 15000.00, 18900, 4210),
(35, 7, '皇后镇周边游', '米尔福德峡湾、瓦卡蒂普湖、格林诺奇，中土世界的壮丽景色...', '/img/悉尼歌剧院.jpg', '新西兰·皇后镇', 'international', 'free', 'published', NOW(), 5, 12000.00, 14500, 2670),

-- 开罗攻略 (destination_id: 36)
(36, 8, '埃及金字塔探秘', '吉萨金字塔群、狮身人面像、埃及博物馆，穿越千年的古文明之旅...', '/img/罗马斗兽场.jpg', '埃及·开罗', 'international', 'free', 'published', NOW(), 4, 8000.00, 11200, 2100),

-- 摩洛哥攻略 (destination_id: 37)
(37, 9, '摩洛哥撒哈拉之旅', '马拉喀什古城、撒哈拉沙漠露营、菲斯迷宫，北非花园的梦幻之旅...', '/img/罗马斗兽场.jpg', '摩洛哥·马拉喀什', 'international', 'free', 'published', NOW(), 7, 12000.00, 13400, 2560),

-- 南非攻略 (destination_id: 38)
(38, 10, '南非彩虹之旅', '桌山、好望角、企鹅滩，彩虹之国的壮丽景色...', '/img/罗马斗兽场.jpg', '南非·开普敦', 'international', 'free', 'published', NOW(), 5, 15000.00, 9800, 1780),

-- 里约热内卢攻略 (destination_id: 39)
(39, 11, '里约热内卢狂欢节', '基督像、糖面包山、科帕卡巴纳海滩，上帝的杰作...', '/img/纽约城市.jpg', '巴西·里约热内卢', 'international', 'free', 'published', NOW(), 4, 12000.00, 15600, 2890),

-- 布宜诺斯艾利斯攻略 (destination_id: 40)
(40, 12, '布宜诺斯艾利斯探戈之旅', '五月广场、雷科莱塔公墓、博卡区，南美巴黎的浪漫与激情...', '/img/纽约城市.jpg', '阿根廷·布宜诺斯艾利斯', 'international', 'free', 'published', NOW(), 5, 10000.00, 11200, 2100),

-- 利马攻略 (destination_id: 41)
(41, 1, '利马美食之旅', '中央市场、米拉弗洛雷斯、巴兰科区，南美美食之都的味蕾盛宴...', '/img/纽约城市.jpg', '秘鲁·利马', 'international', 'free', 'published', NOW(), 3, 6000.00, 8900, 1680);

-- 插入攻略标签（如果不存在）
INSERT IGNORE INTO tags (name) VALUES
('文化'), ('美食'), ('购物'), ('海滩'), ('冒险'), ('蜜月'), ('亲子'), ('自驾'), ('穷游'), ('摄影'), ('历史'), ('艺术'), ('自然'), ('城市'), ('徒步');

-- 为攻略关联标签（使用动态ID）
-- 获取各个标签的ID
SET @tag_culture = (SELECT id FROM tags WHERE name = '文化');
SET @tag_food = (SELECT id FROM tags WHERE name = '美食');
SET @tag_shopping = (SELECT id FROM tags WHERE name = '购物');
SET @tag_beach = (SELECT id FROM tags WHERE name = '海滩');
SET @tag_adventure = (SELECT id FROM tags WHERE name = '冒险');
SET @tag_honeymoon = (SELECT id FROM tags WHERE name = '蜜月');
SET @tag_family = (SELECT id FROM tags WHERE name = '亲子');
SET @tag_selfdrive = (SELECT id FROM tags WHERE name = '自驾');
SET @tag_budget = (SELECT id FROM tags WHERE name = '穷游');
SET @tag_photo = (SELECT id FROM tags WHERE name = '摄影');
SET @tag_history = (SELECT id FROM tags WHERE name = '历史');
SET @tag_art = (SELECT id FROM tags WHERE name = '艺术');
SET @tag_nature = (SELECT id FROM tags WHERE name = '自然');
SET @tag_city = (SELECT id FROM tags WHERE name = '城市');
SET @tag_hike = (SELECT id FROM tags WHERE name = '徒步');

-- 为攻略关联标签（基于插入顺序，ID从@guide_start_id+1开始）
INSERT INTO guide_tags (guide_id, tag_id) VALUES
-- 伦敦攻略标签 (@guide_start_id + 1, +2)
(@guide_start_id + 1, @tag_culture), (@guide_start_id + 1, @tag_history), (@guide_start_id + 1, @tag_city),
(@guide_start_id + 2, @tag_shopping), (@guide_start_id + 2, @tag_city),

-- 阿姆斯特丹攻略标签 (@guide_start_id + 3, +4)
(@guide_start_id + 3, @tag_culture), (@guide_start_id + 3, @tag_art), (@guide_start_id + 3, @tag_selfdrive),
(@guide_start_id + 4, @tag_nature), (@guide_start_id + 4, @tag_art),

-- 布拉格攻略标签 (@guide_start_id + 5, +6)
(@guide_start_id + 5, @tag_culture), (@guide_start_id + 5, @tag_history), (@guide_start_id + 5, @tag_city),
(@guide_start_id + 6, @tag_food), (@guide_start_id + 6, @tag_culture),

-- 维也纳攻略标签 (@guide_start_id + 7, +8)
(@guide_start_id + 7, @tag_culture), (@guide_start_id + 7, @tag_art), (@guide_start_id + 7, @tag_city),
(@guide_start_id + 8, @tag_art), (@guide_start_id + 8, @tag_history),

-- 苏黎世攻略标签 (@guide_start_id + 9)
(@guide_start_id + 9, @tag_city), (@guide_start_id + 9, @tag_nature),

-- 莫斯科攻略标签 (@guide_start_id + 10)
(@guide_start_id + 10, @tag_culture), (@guide_start_id + 10, @tag_history), (@guide_start_id + 10, @tag_city),

-- 马尔代夫攻略标签 (@guide_start_id + 11, +12)
(@guide_start_id + 11, @tag_honeymoon), (@guide_start_id + 11, @tag_beach), (@guide_start_id + 11, @tag_adventure),
(@guide_start_id + 12, @tag_honeymoon), (@guide_start_id + 12, @tag_beach),

-- 河内攻略标签 (@guide_start_id + 13, +14)
(@guide_start_id + 13, @tag_food), (@guide_start_id + 13, @tag_photo),
(@guide_start_id + 14, @tag_adventure), (@guide_start_id + 14, @tag_nature),

-- 曼谷攻略标签 (@guide_start_id + 15, +16)
(@guide_start_id + 15, @tag_culture), (@guide_start_id + 15, @tag_history),
(@guide_start_id + 16, @tag_food), (@guide_start_id + 16, @tag_city),

-- 吉隆坡攻略标签 (@guide_start_id + 17)
(@guide_start_id + 17, @tag_city), (@guide_start_id + 17, @tag_food),

-- 香港攻略标签 (@guide_start_id + 18, +19)
(@guide_start_id + 18, @tag_shopping), (@guide_start_id + 18, @tag_food), (@guide_start_id + 18, @tag_city),
(@guide_start_id + 19, @tag_family), (@guide_start_id + 19, @tag_city),

-- 澳门攻略标签 (@guide_start_id + 20)
(@guide_start_id + 20, @tag_city), (@guide_start_id + 20, @tag_food),

-- 台北攻略标签 (@guide_start_id + 21, +22)
(@guide_start_id + 21, @tag_food), (@guide_start_id + 21, @tag_photo),
(@guide_start_id + 22, @tag_art), (@guide_start_id + 22, @tag_history), (@guide_start_id + 22, @tag_city),

-- 洛杉矶攻略标签 (@guide_start_id + 23, +24)
(@guide_start_id + 23, @tag_city), (@guide_start_id + 23, @tag_adventure),
(@guide_start_id + 24, @tag_shopping), (@guide_start_id + 24, @tag_city),

-- 旧金山攻略标签 (@guide_start_id + 25)
(@guide_start_id + 25, @tag_selfdrive), (@guide_start_id + 25, @tag_city), (@guide_start_id + 25, @tag_food),

-- 温哥华攻略标签 (@guide_start_id + 26)
(@guide_start_id + 26, @tag_nature), (@guide_start_id + 26, @tag_adventure), (@guide_start_id + 26, @tag_photo),

-- 多伦多攻略标签 (@guide_start_id + 27)
(@guide_start_id + 27, @tag_city), (@guide_start_id + 27, @tag_nature),

-- 迈阿密攻略标签 (@guide_start_id + 28)
(@guide_start_id + 28, @tag_beach), (@guide_start_id + 28, @tag_nature), (@guide_start_id + 28, @tag_city),

-- 墨尔本攻略标签 (@guide_start_id + 29, +30)
(@guide_start_id + 29, @tag_food), (@guide_start_id + 29, @tag_city), (@guide_start_id + 29, @tag_photo),
(@guide_start_id + 30, @tag_selfdrive), (@guide_start_id + 30, @tag_nature), (@guide_start_id + 30, @tag_photo),

-- 奥克兰攻略标签 (@guide_start_id + 31)
(@guide_start_id + 31, @tag_city), (@guide_start_id + 31, @tag_adventure),

-- 皇后镇攻略标签 (@guide_start_id + 32, +33)
(@guide_start_id + 32, @tag_adventure), (@guide_start_id + 32, @tag_photo),
(@guide_start_id + 33, @tag_nature), (@guide_start_id + 33, @tag_selfdrive), (@guide_start_id + 33, @tag_photo),

-- 开罗攻略标签 (@guide_start_id + 34)
(@guide_start_id + 34, @tag_culture), (@guide_start_id + 34, @tag_history), (@guide_start_id + 34, @tag_photo),

-- 摩洛哥攻略标签 (@guide_start_id + 35)
(@guide_start_id + 35, @tag_culture), (@guide_start_id + 35, @tag_adventure), (@guide_start_id + 35, @tag_photo),

-- 南非攻略标签 (@guide_start_id + 36)
(@guide_start_id + 36, @tag_nature), (@guide_start_id + 36, @tag_adventure), (@guide_start_id + 36, @tag_photo),

-- 里约热内卢攻略标签 (@guide_start_id + 37)
(@guide_start_id + 37, @tag_city), (@guide_start_id + 37, @tag_beach), (@guide_start_id + 37, @tag_photo),

-- 布宜诺斯艾利斯攻略标签 (@guide_start_id + 38)
(@guide_start_id + 38, @tag_culture), (@guide_start_id + 38, @tag_art), (@guide_start_id + 38, @tag_city),

-- 利马攻略标签 (@guide_start_id + 39)
(@guide_start_id + 39, @tag_food), (@guide_start_id + 39, @tag_photo), (@guide_start_id + 39, @tag_city);
