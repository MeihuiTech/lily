package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.FilDeadlines;
import com.mei.hui.miner.model.FilDeadlinesListVO;

import java.util.List;

/**
 * <p>
 * filcoin 矿工窗口记录表 Mapper 接口
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
public interface FilDeadlinesMapper extends BaseMapper<FilDeadlines> {

    /**
    * 用户首页WindowPoSt的96个窗口
    *
    * @description
    * @author shangbin
    * @date 2021/6/25 16:40
    * @param []
    * @return java.util.List<com.mei.hui.miner.entity.FilDeadlines>
    * @version v1.4.0
    */
    public List<FilDeadlinesListVO> selectFilDeadlinesNinetySixList(String minerId);
}
