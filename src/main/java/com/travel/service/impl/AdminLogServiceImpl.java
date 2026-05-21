package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志查询服务实现
 * 封装后台审计日志的分页查询逻辑
 */
@Service
@RequiredArgsConstructor
public class AdminLogServiceImpl implements AdminLogService {

    private final AdminLogMapper adminLogMapper;
    private static final int PAGE_SIZE = 20;

    @Override
    public PageResult<AdminLog> list(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = adminLogMapper.countAll();
        return PageResult.of(adminLogMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
    }
}
