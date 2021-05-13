package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.MrAggWithdraw;
import com.mei.hui.miner.mapper.MrAggWithdrawMapper;
import com.mei.hui.miner.model.AggWithdrawBO;
import com.mei.hui.miner.model.AggWithdrawVO;
import com.mei.hui.miner.service.MrAggWithdrawService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MrAggWithdrawServiceImpl implements MrAggWithdrawService {

    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private MrAggWithdrawMapper mrAggWithdrawMapper;

    public PageResult<AggWithdrawVO> pageList(AggWithdrawBO input){
        /**
         * 去用于模块模糊查询，获取用户id
         */
        FindSysUsersByNameBO bo = new FindSysUsersByNameBO();
        bo.setName(input.getUserName());
        log.info("模糊查询用户id集合");
        Result<List<FindSysUsersByNameVO>> userResult = userFeignClient.findSysUsersByName(bo);
        log.info("模糊查询用户id集合结果:{}", JSON.toJSONString(userResult));
        List<Long> ids = new ArrayList<>();
        if(ErrorCode.MYB_000000.getCode().equals(userResult.getCode()) && userResult.getData().size() > 0){
            ids = userResult.getData().stream().map(v ->v.getUserId()).collect(Collectors.toList());
        }
        /**
         * 查询用户收益提现分页列表
         */
        QueryWrapper<MrAggWithdraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true,input.isAsc(),input.getCloumName());
        if(ids.size() > 0){
            queryWrapper.in("sys_user_id",ids);
        }
        IPage<MrAggWithdraw> page = mrAggWithdrawMapper
                .selectPage(new Page<>(input.getPageNum(), input.getPageSize()), queryWrapper);
        /**
         * 获取列表后，得到 userId的集合,然后去获取用户 组装到响应中
         */
        List<Long> userIds = page.getRecords().stream().map(v ->v.getSysUserId()).collect(Collectors.toList());
        Map<Long,String> maps = new HashMap<>();
        if(userIds.size() > 0){
            FindSysUserListInput findSysUserListInput = new FindSysUserListInput();
            findSysUserListInput.setUserIds(userIds);
            Result<List<SysUserOut>> users = userFeignClient.findSysUserList(findSysUserListInput);
            users.getData().stream().forEach(v->{
                maps.put(v.getUserId(),v.getUserName());
            });
        }
        List<AggWithdrawVO> lt = page.getRecords().stream().map(v -> {
            AggWithdrawVO vo = new AggWithdrawVO();
            BeanUtils.copyProperties(v, vo);
            vo.setUserName(maps.get(v.getSysUserId()));
            return vo;
        }).collect(Collectors.toList());
        PageResult pageResult = new PageResult(page.getTotal(),lt);
        return pageResult;
    }
}
