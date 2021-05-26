package com.mei.hui.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@TableName("sys_user")
public class SysUser {

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

    private String apiKey;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    @TableField(exist = false)
    private Long[] postIds;

    /** 角色组 */

    @TableField(exist = false)
    private Long[] roleIds;

    /**
     * 权限列表
     */
    @TableField(exist = false)
    private Set<String> permissions;

    @TableField(exist = false)
    private BigDecimal totalBlockAward;

    @TableField(exist = false)
    private BigDecimal powerAvailable;

    /** 角色对象 */
    @TableField(exist = false)
    private List<SysRole> roles;

    @TableField(exist = false)
    @ApiModelProperty(value = "短信验证码")
    private String smsCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务名称,输入字符串")
    private String serviceName;

}
