package com.mei.hui.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiModel
public class SelectUserListInput extends BasePage {

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

    /** 请求参数 */
    private Map<String, Object> params;

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}
