package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.Destination;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 目的地管理服务接口（管理员端）
 * 提供目的地的 CRUD、外键约束检查（有关联攻略时禁止删除）等功能
 */
public interface AdminDestinationService {

    /**
     * 分页查询目的地列表
     *
     * @param page 页码，从1开始
     * @return 分页结果
     */
    PageResult<Destination> list(int page);

    /**
     * 获取目的地详情
     *
     * @param id 目的地ID
     * @return 目的地信息，不存在返回null
     */
    Destination detail(Long id);

    /**
     * 新增目的地
     *
     * @param destination 目的地对象
     * @param session     HTTP会话
     * @param request     HTTP请求
     */
    void create(Destination destination, HttpSession session, HttpServletRequest request);

    /**
     * 修改目的地信息
     *
     * @param id      目的地ID
     * @param body    含name、country、city、description、coverImageUrl
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void update(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);

    /**
     * 删除目的地
     * 如果该目的地下存在关联攻略则拒绝删除
     *
     * @param id      目的地ID
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalStateException 目的地下存在关联攻略
     */
    void delete(Long id, HttpSession session, HttpServletRequest request);
}
