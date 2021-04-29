package com.mei.hui.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色和菜单关联 sys_role_menu
 * @author ruoyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleMenu
{
    /** 角色ID */
    private Long roleId;
    
    /** 菜单ID */
    private Long menuId;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
