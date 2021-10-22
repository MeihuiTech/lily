package com.mei.hui.browser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.browser.entity.FilExGasFeeTrend;
import com.mei.hui.browser.model.FilExGasFeeTrendVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:49
 **/
@Repository
public interface FilExGasFeeTrendMapper extends BaseMapper<FilExGasFeeTrend> {

    /**
     * 近3小时封装Gas费用(Fil/TiB)
     * @return
     */
    public List<FilExGasFeeTrend> selectThirdGasFeeList(Long beforeThirdSecond);
}
