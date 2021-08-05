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

    String CURRENCYID = "currency_id";
    String PLATFORM = "platform";
    String ISVISITOR = "isVisitor";
    String ROLEIDS = "RoleIds";


    String ACCESSKEY = "ACCESSKEY";

    //短信验证码5分钟有效，格式sms:serviceName:userId
    String SMSKEY="sms:%s:%s";

    //短信验证码1分钟后可以重新发送，时间格式sms:serviceName:userIdtime
    String SMSKEYTIME="sms:%s:%stime";

    String KEY_ALGORITHM = "AES";

    String RSA = "RSA";
    /**
     * 默认的加密算法
     */
    String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 供上报接口确定唯一性用
     */
    String APIKEY = "x-api-key";

    /**
     * redis保存的key：扇区状态持续时间
     */
    String SECTORDURATIONKEY = "fil:sectorduration:%s:%s";

}
