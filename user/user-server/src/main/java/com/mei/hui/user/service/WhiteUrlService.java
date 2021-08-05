package com.mei.hui.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.user.entity.WhiteUrl;

import java.util.List;

/**
 * <p>
 * 白名单 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-30
 */
public interface WhiteUrlService extends IService<WhiteUrl> {

    /**
     * 校验规则：
     * 1：先校验是否是白名单url,如果是则返回true;否则，校验是否拥有当前请求url的权限
     * @return
     */
    boolean checkAutoUrl(String url,List<Long> roleIds);

}
