package com.mei.hui.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.user.entity.ApiUser;
import com.mei.hui.util.Result;

/**
 * <p>
 * 对外api调用客户信息配置 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-14
 */
public interface ApiUserService extends IService<ApiUser> {

    Result getToken(String body);

}
