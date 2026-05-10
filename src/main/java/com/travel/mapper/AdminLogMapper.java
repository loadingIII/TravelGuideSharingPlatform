package com.travel.mapper;

import com.travel.pojo.model.AdminLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminLogMapper {

    @Insert("""
            INSERT INTO admin_logs (admin_id, admin_username, action, target_type, target_id, detail, ip_address)
            VALUES (#{adminId}, #{adminUsername}, #{action}, #{targetType}, #{targetId}, #{detail}, #{ipAddress})
            """)
    int insert(AdminLog log);

    @Select("SELECT * FROM admin_logs ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<AdminLog> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM admin_logs")
    long countAll();
}
