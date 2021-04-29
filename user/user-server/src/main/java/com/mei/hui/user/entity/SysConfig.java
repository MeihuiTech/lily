package com.mei.hui.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mei.hui.util.BasePage;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数配置表 sys_config
 * @author ruoyi
 */
@Data
public class SysConfig extends BasePage
{

    /** 参数主键 */
    private Long configId;

    /** 参数名称 */
    private String configName;

    /** 参数键名 */
    private String configKey;

    /** 参数键值 */
    private String configValue;

    /** 系统内置（Y是 N否） */
    private String configType;

    private String createBy;

    private String updateBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


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

}
