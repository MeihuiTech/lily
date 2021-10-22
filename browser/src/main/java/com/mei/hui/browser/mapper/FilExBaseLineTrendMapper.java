package com.mei.hui.browser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.browser.entity.FilExBaseLineTrend;
import com.mei.hui.browser.entity.FilExOverview;
import com.mei.hui.browser.model.FilExBaseLineTrendVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:49
 **/
@Repository
public interface FilExBaseLineTrendMapper extends BaseMapper<FilExBaseLineTrend> {

    /**
     * 近30天有效算力走势
     * @return
     */
    public List<FilExBaseLineTrend> selectThirdDayPower();
}
