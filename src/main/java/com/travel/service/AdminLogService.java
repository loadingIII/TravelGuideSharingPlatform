package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.AdminLog;

/**
 * 操作日志查询服务接口
 * 提供后台审计日志的分页查询功能
 */
public interface AdminLogService {

    /**
     * 分页查询操作日志
     *
     * @param page 页码，从1开始
     * @return 分页结果
     */
    PageResult<AdminLog> list(int page);
}
