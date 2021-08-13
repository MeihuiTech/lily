package com.mei.hui.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mei.hui.miner.feign.vo.CurrencyRateBO;
import com.mei.hui.user.entity.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public class AddSysUserBO {

    @ApiModelProperty(value = "用户名",required = true)
    private String userName;

    @ApiModelProperty(value = "手机号",required = true)
    private String phonenumber;

    @ApiModelProperty(value = "密码",required = true)
    private String password;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "角色id",required = true)
    private Long roleIds;

    @ApiModelProperty(value = "币种费率集合",required = true)
    List<CurrencyRateBO> rats = new ArrayList<>();

    @ApiModelProperty(value = "邮箱")
    private String email;
}
