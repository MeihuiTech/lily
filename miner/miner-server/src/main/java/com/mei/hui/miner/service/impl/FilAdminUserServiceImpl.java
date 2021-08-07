package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilAdminUser;
import com.mei.hui.miner.feign.vo.AdminUserPageBO;
import com.mei.hui.miner.feign.vo.UpdateAdminUserBO;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.mapper.FilAdminUserMapper;
import com.mei.hui.miner.service.FilAdminUserService;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理员和用户对应关系 服务实现类
 * </p>
 * @author 鲍红建
 * @since 2021-08-06
 */
@Service
@Slf4j
public class FilAdminUserServiceImpl extends ServiceImpl<FilAdminUserMapper, FilAdminUser> implements FilAdminUserService {
    @Autowired
    private UserManager userManager;

    public PageResult<AdminUserPageBO> adminUserPage(BasePage basePage){
        PageResult<SysUserOut> page = userManager.findAllAdminUser(basePage);
        List<Long> userIds = page.getRows().stream().map(v -> v.getUserId()).collect(Collectors.toList());
        log.info("查询管理员分页列表:{}", JSON.toJSONString(userIds));

        List<SysUserOut> allUser = userManager.findAllUser();
        Map<Long,String> userMap = new HashMap<>();
        allUser.stream().forEach(v->userMap.put(v.getUserId(),v.getUserName()));
        log.info("查询所有用户:{}",JSON.toJSONString(userMap));

        LambdaQueryWrapper<FilAdminUser> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(FilAdminUser::getAdminId,userIds);
        List<FilAdminUser> list = this.list(queryWrapper);

        Map<Long,AdminUserPageBO> pageMap = new HashMap<>();
        list.stream().forEach(v -> {
            AdminUserPageBO adminUserPageBO = new AdminUserPageBO();
            adminUserPageBO.setAdminId(v.getAdminId());
            adminUserPageBO.setAdminName(userMap.get(v.getAdminId()));
            adminUserPageBO.setUserId(v.getUserId());
            adminUserPageBO.setUserName(userMap.get(v.getUserId()));
            pageMap.put(v.getAdminId(),adminUserPageBO);
        });

        List<AdminUserPageBO> resultList = new ArrayList<>();
        //返回把没有分配矿工用户的管理员
        page.getRows().stream().forEach(v->{
            AdminUserPageBO vo = pageMap.get(v.getUserId());
            if(vo == null){
                AdminUserPageBO bo = new AdminUserPageBO().setAdminId(v.getUserId()).setAdminName(v.getUserName());
                resultList.add(bo);
            }else{
                resultList.add(vo);
            }
        });
        return new PageResult(page.getTotal(),resultList);
    }

    public Result saveOrUpdateAdmin(@RequestBody UpdateAdminUserBO bo){
        LambdaQueryWrapper<FilAdminUser> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FilAdminUser::getAdminId,bo.getAdminId());
        queryWrapper.eq(FilAdminUser::getUserId,bo.getUserId());
        List<FilAdminUser> list = this.list(queryWrapper);
        log.info("管理员管理的普通用户:{}",JSON.toJSONString(list));
        if(list.size() <= 0){
            FilAdminUser adminUser = new FilAdminUser();
            adminUser.setAdminId(bo.getAdminId());
            adminUser.setUserId(bo.getUserId());
            adminUser.setCreateTime(LocalDateTime.now());
            this.save(adminUser);
        }else{
            this.removeById(list.get(0).getId());
        }
        return Result.OK;
    }

    /**
     * 获取管理员负责管理的用户id 列表
     * @return
     */
    public List<Long> findUserIdsByAdmin(){
        Long userId = HttpRequestUtil.getUserId();
        LambdaQueryWrapper<FilAdminUser> adminQuery = new LambdaQueryWrapper();
        adminQuery.eq(FilAdminUser::getAdminId,userId);
        List<FilAdminUser> admins = this.list(adminQuery);
        List<Long> userIds = admins.stream().map(v -> v.getUserId()).collect(Collectors.toList());
        log.info("管理员:{},负责的用户:{}",userId,JSON.toJSONString(userIds));
        return userIds;
    }

}
