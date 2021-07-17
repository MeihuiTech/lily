package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.feign.vo.MinerIpLongitudeLatitudeBO;
import com.mei.hui.miner.feign.vo.MinerLongitudeLatitudeVO;
import com.mei.hui.util.Result;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/16 17:51
 **/
public interface IMinerLongitudeLatitudeService extends IService<MinerLongitudeLatitude> {

    /**
    * 上报矿工ip
    *
    * @description
    * @author shangbin
    * @date 2021/7/16 19:05
    * @param [minerIpLongitudeLatitudeBO]
    * @return java.lang.Integer
    * @version v1.4.1
    */
    public Integer reportMinerIpLongitudeLatitude(MinerIpLongitudeLatitudeBO minerIpLongitudeLatitudeBO);

    /**
    * 查询fil矿工id节点地图
    *
    * @description
    * @author shangbin
    * @date 2021/7/17 10:42
    * @param []
    * @return com.mei.hui.util.Result
    * @version v1.4.1
    */
    public List<MinerLongitudeLatitudeVO> selectMap();
}
