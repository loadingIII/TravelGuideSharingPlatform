package com.travel.mapper;

import com.travel.pojo.model.AdminLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminLogMapper {

    @Insert("""
            INSERT INTO admin_logs (admin_id, admin_username, action, target_type, target_id, detail, ip_address)
            VALUES (#{adminId}, #{adminUsername}, #{action}, #{targetType}, #{targetId}, #{detail}, #{ipAddress})
            """)
    int insert(AdminLog log);
}
