package com.mei.hui.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * 用户和角色关联 sys_user_role
 * @author ruoyi
 */
@Data
@TableName("sys_user_role")
public class SysUserRole
{
    /** 用户ID */
    private Long userId;
    
    /** 角色ID */
    private Long roleId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
