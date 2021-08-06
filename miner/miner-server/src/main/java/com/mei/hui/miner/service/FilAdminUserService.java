package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilAdminUser;
import com.mei.hui.miner.feign.vo.AdminUserPageBO;
import com.mei.hui.miner.feign.vo.UpdateAdminUserBO;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 管理员和用户对应关系 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-08-06
 */
public interface FilAdminUserService extends IService<FilAdminUser> {

    PageResult<AdminUserPageBO> adminUserPage(BasePage basePage);
    Result saveOrUpdateAdmin(@RequestBody UpdateAdminUserBO bo);
}
