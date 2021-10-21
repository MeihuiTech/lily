package com.mei.hui.browser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.browser.entity.FilExBaseLineTrend;
import com.mei.hui.browser.model.FilExBaseLineTrendVO;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:52
 **/
public interface FilExBaseLineTrendService extends IService<FilExBaseLineTrend> {

    /**
     * 近30天有效算力走势
     * @return
     */
    public List<FilExBaseLineTrendVO> selectThirdDayPower();
}
