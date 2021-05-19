package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysReceiveAddress;
import com.mei.hui.miner.mapper.SysReceiveAddressMapper;
import com.mei.hui.miner.model.SysReceiveAddressBO;
import com.mei.hui.miner.model.SysReceiveAddressVO;
import com.mei.hui.miner.model.UpdateReceiveAddressBO;
import com.mei.hui.miner.service.ISysReceiveAddressService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收款地址表
 */
@Slf4j
@Service
public class SysReceiveAddressServiceImpl implements ISysReceiveAddressService {
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private SysReceiveAddressMapper sysReceiveAddressMapper;
    /**
     * 新增地址
     * @param sysReceiveAddress
     * @return
     */
    public Result addReceiveAddress(SysReceiveAddressBO sysReceiveAddress) {
        Long userId = HttpRequestUtil.getUserId();
        //检查验证码是否正确
        String code = redisUtils.get(sysReceiveAddress.getServiceName() + "_" + userId);
        if(StringUtils.isEmpty(code)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码已失效");
        }
        if(!code.equals(sysReceiveAddress.getSmsCode())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        //新增地址
        SysReceiveAddress  receiveAddress= new SysReceiveAddress();
        receiveAddress.setUserId(userId);
        receiveAddress.setCurrencyId(sysReceiveAddress.getCurrencyId());
        receiveAddress.setAddress(sysReceiveAddress.getAddress());
        receiveAddress.setRemark(sysReceiveAddress.getRemark());
        receiveAddress.setCreateTime(LocalDateTime.now());
        receiveAddress.setUpdateTime(LocalDateTime.now());
        int rows = sysReceiveAddressMapper.insert(receiveAddress);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
    * 根据id查询没有被删除的收款地址
    * @description
    * @author shangbin
    * @date 2021/5/14 13:54
    * @return com.mei.hui.miner.entity.SysReceiveAddress
    * @version v1.0.0
    */
    public Result<SysReceiveAddressVO> selectSysReceiveAddressById(Long id) {
        SysReceiveAddress dbSysReceiveAddress = sysReceiveAddressMapper.selectById(id);
        if (dbSysReceiveAddress == null) {
            return Result.fail(MinerError.MYB_222222.getCode(),"当前收款地址不存在");
        }
        SysReceiveAddressVO sysReceiveAddressVO = new SysReceiveAddressVO();
        BeanUtils.copyProperties(dbSysReceiveAddress,sysReceiveAddressVO);
        return Result.success(sysReceiveAddressVO);
    }

    /**
    * 编辑收款地址，先逻辑删除，后新增
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:06
    * @return int
    * @version v1.0.0
    */
    public Result updateReceiveAddress(UpdateReceiveAddressBO bo) {
        Long userId = HttpRequestUtil.getUserId();
        //检查验证码是否正确
        String code = redisUtils.get(String.format(SystemConstants.SMSKKEY,bo.getServiceName(),userId));
        if(StringUtils.isEmpty(code)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码已失效");
        }
        if(!code.equals(bo.getSmsCode())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        //更新
        LambdaQueryWrapper<SysReceiveAddress> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysReceiveAddress::getUserId,userId);
        queryWrapper.eq(SysReceiveAddress::getCurrencyId,bo.getCurrencyId());
        List<SysReceiveAddress> address = sysReceiveAddressMapper.selectList(queryWrapper);
        if (address.size() == 0) {
            return Result.fail(MinerError.MYB_222222.getCode(),"当前收款地址不存在");
        }
        SysReceiveAddress sysReceiveAddress = address.get(0);
        LambdaUpdateWrapper<SysReceiveAddress> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(SysReceiveAddress::getId,sysReceiveAddress.getId());
        updateWrapper.set(SysReceiveAddress::getAddress,bo.getAddress());
        updateWrapper.set(SysReceiveAddress::getRemark,bo.getRemark());
        int rows = sysReceiveAddressMapper.update(null,updateWrapper);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }
}
