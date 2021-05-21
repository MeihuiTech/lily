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

    //短信验证码5分钟有效，格式sms:serviceName:userId
    String SMSKEY="sms:%s:%s";

    //短信验证码1分钟后可以重新发送，时间格式sms:serviceName:userIdtime
    String SMSKEYTIME="sms:%s:%stime";

    String KEY_ALGORITHM = "AES";
    /**
     * 默认的加密算法
     */
    String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

}
