package com.mei.hui.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * 角色和部门关联 sys_role_dept
 * 
 * @author ruoyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleDept
{
    /** 角色ID */
    private Long roleId;
    
    /** 部门ID */
    private Long deptId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
