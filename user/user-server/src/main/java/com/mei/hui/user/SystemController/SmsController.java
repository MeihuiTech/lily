package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.model.SmsSendBO;
import com.mei.hui.user.service.SmsService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "发送手机短信公共接口【鲍红建】")
@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;

    @ApiOperation("发送验证码,添加地址:add_receive_address;修改个人信息:edit_user_info")
    @PostMapping("/send")
    public Result send(@RequestBody SmsSendBO smsSendBO){
        if(smsSendBO == null || StringUtils.isEmpty(smsSendBO.getServiceName())){
            throw MyException.fail(UserError.MYB_333333.getCode(),"业务名称不能为空");
        }
        return smsService.send(smsSendBO);
    }
}
