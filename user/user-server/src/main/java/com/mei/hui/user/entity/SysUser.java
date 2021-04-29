package com.mei.hui.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mei.hui.util.BasePage;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@TableName("sys_user")
public class SysUser extends BasePage {

    @TableId(type= IdType.AUTO)
    private Long userId;

    private String userName;

    private String nickName;

    private String userType;

    private String email;

    private String phonenumber;

    private Integer sex;

    private String avatar;

    private String password;

    private String status;

    private String delFlag;

    private BigDecimal feeRate;

    private String loginIp;

    private LocalDateTime loginDate;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private String remark;

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /** 岗位组 */
    private Long[] postIds;

    /** 角色组 */
    private Long[] roleIds;

    /**
     * 权限列表
     */
    private Set<String> permissions;

}
