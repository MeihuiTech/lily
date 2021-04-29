package com.mei.hui.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mei.hui.user.common.Base64;
import com.mei.hui.util.BasePage;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位表 sys_post
 * @author ruoyi
 */
@TableName("sys_post")
@Data
public class SysPost extends BasePage {

    /** 岗位序号 */
    @TableId(type= IdType.AUTO)
    private Long postId;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */
    private String postName;

    /** 岗位排序 */
    private String postSort;

    /** 状态（0正常 1停用） */
    private String status;

    /** 用户是否存在此岗位标识 默认不存在 */
    private boolean flag = false;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

}
