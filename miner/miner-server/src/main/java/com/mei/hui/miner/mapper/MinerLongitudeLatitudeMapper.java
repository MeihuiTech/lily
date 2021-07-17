package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.feign.vo.MinerLongitudeLatitudeVO;

import java.util.List;


public interface MinerLongitudeLatitudeMapper extends BaseMapper<MinerLongitudeLatitude> {

    /**
    * 查询fil矿工id节点地图
    *
    * @description
    * @author shangbin
    * @date 2021/7/17 10:51
    * @param []
    * @return java.util.List<com.mei.hui.miner.feign.vo.MinerLongitudeLatitudeVO>
    * @version v1.4.1
    */
    public List<MinerLongitudeLatitudeVO> selectMinerLongitudeLatitudeVOList();
}