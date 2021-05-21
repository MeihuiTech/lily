package com.mei.hui.user.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ExportLoginin {

    @ExcelProperty(value = "ID",index = 0)
    private Long infoId;

    @ExcelProperty(value = "用户账号",index = 1)
    private String userName;

    /** 登录状态 0成功 1失败 */
    @ExcelProperty(value = "用户账号",index = 2)
    private String status;

    /** 登录IP地址 */
    @ExcelProperty(value = "登录IP地址",index = 3)
    private String ipaddr;

    /** 登录地点 */
    @ExcelProperty(value = "登录地点",index = 4)
    private String loginLocation;

    /** 浏览器类型 */
    @ExcelProperty(value = "浏览器类型",index = 5)
    private String browser;

    /** 操作系统 */
    @ExcelProperty(value = "操作系统",index = 6)
    private String os;

    /** 提示消息 */
    @ExcelProperty(value = "提示消息",index = 7)
    private String msg;

    /** 访问时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "访问时间",index = 8)
    private Date loginTime;
}
