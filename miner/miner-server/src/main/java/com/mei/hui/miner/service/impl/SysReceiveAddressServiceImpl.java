package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
     * 新增收款地址
     * @param sysReceiveAddress
     * @return
     */
    @Override
    public Result addReceiveAddress(SysReceiveAddressBO sysReceiveAddress) {
        Long userId = HttpRequestUtil.getUserId();
        //检查验证码是否正确
        String smsCode = String.format(SystemConstants.SMSKEY,sysReceiveAddress.getServiceName(),userId);
        log.info("验证码key:【{}】",smsCode);
        String code = redisUtils.get(smsCode);
        log.info("验证码value:【{}】",code);
        if(StringUtils.isEmpty(code)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        if(!code.equals(sysReceiveAddress.getSmsCode())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }

        //单一币种只添加1个收款地址
        SysReceiveAddress dbSysReceiveAddress = new SysReceiveAddress();
        dbSysReceiveAddress.setUserId(userId);
        dbSysReceiveAddress.setCurrencyId(sysReceiveAddress.getCurrencyId());
        QueryWrapper<SysReceiveAddress> sysReceiveAddressQueryWrapper = new QueryWrapper<>();
        sysReceiveAddressQueryWrapper.setEntity(dbSysReceiveAddress);
        List<SysReceiveAddress> dbSysReceiveAddressList = sysReceiveAddressMapper.selectList(sysReceiveAddressQueryWrapper);
        log.info("收款地址出参：【{}】",JSON.toJSON(dbSysReceiveAddressList));
        if (dbSysReceiveAddressList != null && dbSysReceiveAddressList.size() > 0) {
            return Result.fail(MinerError.MYB_222222.getCode(),"单一币种只添加1个收款地址");
        }

        //新增地址
        SysReceiveAddress  receiveAddress= new SysReceiveAddress();
        receiveAddress.setUserId(userId);
        receiveAddress.setCurrencyId(sysReceiveAddress.getCurrencyId());
        receiveAddress.setAddress(sysReceiveAddress.getAddress());
//        receiveAddress.setRemark(sysReceiveAddress.getRemark());
        receiveAddress.setCreateTime(LocalDateTime.now());
        log.info("新增收款地址:【{}】",JSON.toJSON(receiveAddress));
        int rows = sysReceiveAddressMapper.insert(receiveAddress);
        log.info("新增收款地址出参：【{}】",rows);

        // 清空验证码
        log.info("清空验证码smsCode:【{}】,code:【{}】",smsCode,code);
        redisUtils.delete(smsCode);
        String smsCodeTime = String.format(SystemConstants.SMSKEYTIME,sysReceiveAddress.getServiceName(),userId);
        log.info("清空验证码smsCodeTime:【{}】",smsCodeTime);
        redisUtils.delete(smsCodeTime);

        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
    * 根据id查询收款地址
    * @description
    * @author shangbin
    * @date 2021/5/14 13:54
    * @return com.mei.hui.miner.entity.SysReceiveAddress
    * @version v1.0.0
    */
    @Override
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
    * 编辑收款地址
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:06
    * @return int
    * @version v1.0.0
    */
    @Override
    public Result updateReceiveAddress(UpdateReceiveAddressBO bo) {
        Long userId = HttpRequestUtil.getUserId();
        //检查验证码是否正确
        String smsCode =String.format(SystemConstants.SMSKEY,bo.getServiceName(),userId);
        log.info("验证码key:【{}】",smsCode);
        String code = redisUtils.get(smsCode);
        log.info("验证码value:【{}】",code);
        if(StringUtils.isEmpty(code)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        if(!code.equals(bo.getSmsCode())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        //更新
        LambdaQueryWrapper<SysReceiveAddress> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysReceiveAddress::getUserId,userId);
        queryWrapper.eq(SysReceiveAddress::getCurrencyId,bo.getCurrencyId());
        List<SysReceiveAddress> address = sysReceiveAddressMapper.selectList(queryWrapper);
        log.info("收款地址表出参：【{}】",JSON.toJSON(address));
        if (address.size() == 0) {
            return Result.fail(MinerError.MYB_222222.getCode(),"当前收款地址不存在");
        }
        SysReceiveAddress sysReceiveAddress = address.get(0);
        LambdaUpdateWrapper<SysReceiveAddress> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(SysReceiveAddress::getId,sysReceiveAddress.getId());
        updateWrapper.set(SysReceiveAddress::getAddress,bo.getAddress());
        updateWrapper.set(SysReceiveAddress::getUpdateTime,LocalDateTime.now());
//        updateWrapper.set(SysReceiveAddress::getRemark,bo.getRemark());
        int rows = sysReceiveAddressMapper.update(null,updateWrapper);
        log.info("编辑收款地址:【{}】",rows);

        // 清空验证码
        log.info("清空验证码smsCode:【{}】,code:【{}】",smsCode,code);
        redisUtils.delete(smsCode);
        String smsCodeTime = String.format(SystemConstants.SMSKEYTIME,bo.getServiceName(),userId);
        log.info("清空验证码smsCodeTime:【{}】",smsCodeTime);
        redisUtils.delete(smsCodeTime);

        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
    * 根据币种id查询收款地址
    *
    * @description
    * @author shangbin
    * @date 2021/5/21 18:52
    * @param [currencyId]
    * @return com.mei.hui.util.Result<com.mei.hui.miner.model.SysReceiveAddressVO>
    * @version v1.0.0
    */
    @Override
    public Result<SysReceiveAddressVO> selectSysReceiveAddressByCurrencyId(Long currencyId) {
        Long userId = HttpRequestUtil.getUserId();
        SysReceiveAddress sysReceiveAddress = new SysReceiveAddress();
        sysReceiveAddress.setUserId(userId);
        sysReceiveAddress.setCurrencyId(HttpRequestUtil.getCurrencyId());
        QueryWrapper<SysReceiveAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(sysReceiveAddress);
        List<SysReceiveAddress> sysReceiveAddressList = sysReceiveAddressMapper.selectList(queryWrapper);
        log.info("收款地址表出参：【{}】",JSON.toJSON(sysReceiveAddressList));
        if (sysReceiveAddressList != null && sysReceiveAddressList.size() > 0) {
            SysReceiveAddressVO sysReceiveAddressVO = new SysReceiveAddressVO();
            BeanUtils.copyProperties(sysReceiveAddressList.get(0),sysReceiveAddressVO);
            return Result.success(sysReceiveAddressVO);
        }
        return Result.OK;
    }



}
