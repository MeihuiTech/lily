package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.entity.SysReceiveAddress;
import com.mei.hui.miner.mapper.SysReceiveAddressMapper;
import com.mei.hui.miner.service.ISysReceiveAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 收款地址表
 */
@Slf4j
@Service
public class SysReceiveAddressServiceImpl implements ISysReceiveAddressService {

    @Autowired
    private SysReceiveAddressMapper sysReceiveAddressMapper;

    /*
    *
    * @description 新增收款地址
    * @author shangbin
    * @date 2021/5/14 11:31
    * @param [sysReceiveAddress]
    * @return int
    * @version v1.0.0
    */
    @Override
    public int addReceiveAddress(SysReceiveAddress sysReceiveAddress) {
        log.info("新增收款地址：【{}】", JSON.toJSON(sysReceiveAddress));
        return sysReceiveAddressMapper.insert(sysReceiveAddress);
    }

    /**
    * 根据id查询没有被删除的收款地址
    * @description
    * @author shangbin
    * @date 2021/5/14 13:54
    * @param [id]
    * @return com.mei.hui.miner.entity.SysReceiveAddress
    * @version v1.0.0
    */
    @Override
    public SysReceiveAddress selectSysReceiveAddressById(Long id) {
        SysReceiveAddress sysReceiveAddress = sysReceiveAddressMapper.selectById(id);
        if (sysReceiveAddress.getDelFlag()) {
            return null;
        }
        return sysReceiveAddress;
    }

    /**
    * 编辑收款地址，先逻辑删除，后新增
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:06
    * @param [sysReceiveAddress]
    * @return int
    * @version v1.0.0
    */
    @Override
    public int updateReceiveAddress(SysReceiveAddress sysReceiveAddress) {
//        SysReceiveAddress updateSysReceiveAddress = new SysReceiveAddress();
//        BeanUtils.copyProperties(sysReceiveAddress,updateSysReceiveAddress);
        sysReceiveAddress.setDelFlag(true);
        sysReceiveAddress.setUpdateTime(new Date());
        log.info("先逻辑删除收款地址：【{}】",JSON.toJSON(sysReceiveAddress));
        sysReceiveAddressMapper.updateById(sysReceiveAddress);
        // TODO 一会修改这
        sysReceiveAddress.setId(null);
        sysReceiveAddress.setDelFlag(false);
        sysReceiveAddress.setUpdateTime(null);
        log.info("新增收款地址：【{}】",JSON.toJSON(sysReceiveAddress));
        return sysReceiveAddressMapper.insert(sysReceiveAddress);
    }
}
