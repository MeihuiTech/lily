package com.mei.hui.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 白名单
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-30
 */
@Data
@Accessors(chain = true)
@TableName("white_url")
public class WhiteUrl implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 地址
     */
    private String url;

    /**
     * 备注
     */
    private String remark;


}
