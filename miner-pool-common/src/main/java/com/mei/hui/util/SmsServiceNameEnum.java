package com.mei.hui.util;

/**
 * 验证码ServiceName枚举类
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/19 11:26
 **/
public enum  SmsServiceNameEnum {

    withdraw("提取收益"),
    addReceiveAddress("新增收款地址"),
    updateReceiveAddress("编辑收款地址"),
    updateUser("修改用户信息");


    private String serviceName;

    SmsServiceNameEnum(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
