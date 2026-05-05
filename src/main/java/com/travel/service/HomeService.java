package com.travel.service;


import com.travel.pojo.vo.HomeVO;
import com.travel.pojo.vo.SearchVO;

/**
 * 首页服务接口
 * 处理首页数据展示和搜索功能的业务逻辑
 */
public interface HomeService {

    /**
     * 获取首页数据
     * 返回首页展示所需的所有数据，包括推荐目的地、热门攻略、推荐内容等
     *
     * @return 首页综合数据
     */
    HomeVO getHome();

    /**
     * 全局搜索
     * 根据关键词搜索攻略、目的地等内容
     *
     * @param query 搜索关键词
     * @param type  搜索类型：all（全部）、guide（攻略）、destination（目的地）
     * @return 搜索结果
     */
    SearchVO search(String query, String type);
}
