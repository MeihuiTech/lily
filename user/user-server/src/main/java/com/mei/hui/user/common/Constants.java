package com.mei.hui.user.common;

import com.mei.hui.util.SystemConstants;

public interface Constants extends SystemConstants {

    /**
     * 验证码 redis key
     */
    String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
   Integer CAPTCHA_EXPIRATION = 2;

    /** 菜单类型（目录） */
    String TYPE_DIR = "M";

    /** 菜单类型（菜单） */
    String TYPE_MENU = "C";

    /** 是否菜单外链（否） */
   String NO_FRAME = "1";

    /** Layout组件标识 */
    String LAYOUT = "Layout";

    /** ParentView组件标识 */
    String PARENT_VIEW = "ParentView";


    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /** 校验返回结果码 */
    String NOT_UNIQUE = "1";

    /** 是否为系统默认（是） */
    String YES = "Y";

    /** 是否菜单外链（是） */
    String YES_FRAME = "0";

    /**
     * 资源映射路径 前缀
     */
    String RESOURCE_PREFIX = "/profile";

   /**
    * offline:userId
    */
    String OfflineUser="offline:%s";

    /**
     * fil 币的币种id
     */
    Long fileCurrencyId = 1L;


}
