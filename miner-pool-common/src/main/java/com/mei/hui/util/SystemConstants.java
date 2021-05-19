package com.mei.hui.util;

public interface SystemConstants {

    /**
     * 令牌
     */
    String TOKEN = "token";

    /**
     * 放入token 中的数据
     */
    String USERID = "userId";
    //帐号状态（0正常 1停用）
    String STATUS = "status";
    String DELFLAG = "del_flag";
    String PLATFORM = "platform";

    String WEB = "web";
    String APP = "app";

    //短信验证码格式sms:serviceName:userId
    String SMSKKEY="sms:%s:%s";


}
