package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysReceiveAddress;
import com.mei.hui.miner.model.SysReceiveAddressBO;
import com.mei.hui.miner.model.SysReceiveAddressVO;
import com.mei.hui.miner.model.UpdateReceiveAddressBO;
import com.mei.hui.util.Result;

/*
*
* @description 收款地址表
* @author shangbin
* @date 2021/5/14 10:37
* @version v1.0.0
*/
public interface ISysReceiveAddressService {


    /**
    * 新增收款地址
    * @description
    * @author shangbin
    * @date 2021/5/14 11:30
    * @return int
    * @version v1.0.0
    */
    Result addReceiveAddress(SysReceiveAddressBO sysReceiveAddress);

    /**
    * 根据id查询没有被删除的收款地址
    * @description
    * @author shangbin
    * @date 2021/5/14 11:46
    * @return com.mei.hui.miner.entity.SysReceiveAddress
    * @version v1.0.0
    */
    Result<SysReceiveAddressVO> selectSysReceiveAddressById(Long id);

    /**
    * 编辑收款地址
    * @description
    * @author shangbin
    * @date 2021/5/14 13:37
    * @return int
    * @version v1.0.0
    */
    Result updateReceiveAddress(UpdateReceiveAddressBO bo);
}
