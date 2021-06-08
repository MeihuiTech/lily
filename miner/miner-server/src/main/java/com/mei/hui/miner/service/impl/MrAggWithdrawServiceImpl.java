package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.common.MinerError;
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
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MrAggWithdrawServiceImpl implements MrAggWithdrawService {

    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private MrAggWithdrawMapper mrAggWithdrawMapper;


    /**
    * 用户收益提现汇总分页查询
    *
    * @description
    * @author shangbin
    * @date 2021/6/4 16:50
    * @param [input]
    * @return com.mei.hui.util.PageResult<com.mei.hui.miner.model.AggWithdrawVO>
    * @version v1.0.0
    */
    @Override
    public PageResult<AggWithdrawVO> pageList(AggWithdrawBO input){
        //查询用户收益提现分页列表
        LambdaQueryWrapper<MrAggWithdraw> queryWrapper = new LambdaQueryWrapper<>();
        //排序
        if("totalFee".equals(input.getCloumName())){
            if(input.isAsc()){
                //true升序
                queryWrapper.orderByAsc(MrAggWithdraw::getTotalFee);
            }else {
                //降序
                queryWrapper.orderByDesc(MrAggWithdraw::getTotalFee);
            }
        } else if("tatalCount".equals(input.getCloumName())){
            if(input.isAsc()){
                //true升序
                queryWrapper.orderByAsc(MrAggWithdraw::getTatalCount);
            }else {
                //降序
                queryWrapper.orderByDesc(MrAggWithdraw::getTatalCount);
            }
        } else if("takeTotalMony".equals(input.getCloumName())){
            if(input.isAsc()){
                //true升序
                queryWrapper.orderByAsc(MrAggWithdraw::getTakeTotalMony);
            }else {
                //降序
                queryWrapper.orderByDesc(MrAggWithdraw::getTakeTotalMony);
            }
        } else {
            queryWrapper.orderByDesc(MrAggWithdraw::getTakeTotalMony);
        }

        //用于入参模块模糊查询，获取用户id
        String userName = input.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
            FindSysUsersByNameBO bo = new FindSysUsersByNameBO();
            bo.setName(input.getUserName());
            log.info("模糊查询用户id集合");
            Result<List<FindSysUsersByNameVO>> userResult = userFeignClient.findSysUsersByName(bo);
            log.info("模糊查询用户id集合结果:{}", JSON.toJSONString(userResult));
            List<Long> idList = new ArrayList<>();
            if(ErrorCode.MYB_000000.getCode().equals(userResult.getCode()) && userResult.getData().size() > 0){
                idList = userResult.getData().stream().map(v ->v.getUserId()).collect(Collectors.toList());
                // id去重
                Set<Long> idsSet = new HashSet<>(idList);
                queryWrapper.in(MrAggWithdraw::getSysUserId,new ArrayList<Long>(idsSet));
            } else {
                return new PageResult(0,new ArrayList());
            }
        }

        // 查询条件币种
        Long currencyId = input.getCurrencyId();
        if (currencyId != null) {
            CurrencyEnum currencyEnum = CurrencyEnum.getCurrency(currencyId);
            if (currencyEnum == null) {
                throw MyException.fail(MinerError.MYB_222222.getCode(),"入参币种不存在");
            }
            String currencyType = currencyEnum.name();
            queryWrapper.eq(MrAggWithdraw::getType,currencyType);
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
            vo.setType(vo.getType());
            return vo;
        }).collect(Collectors.toList());
        PageResult pageResult = new PageResult(page.getTotal(),lt);
        return pageResult;
    }
}
