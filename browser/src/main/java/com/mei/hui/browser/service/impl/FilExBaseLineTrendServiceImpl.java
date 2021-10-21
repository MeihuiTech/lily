package com.mei.hui.browser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.browser.entity.FilExBaseLineTrend;
import com.mei.hui.browser.mapper.FilExBaseLineTrendMapper;
import com.mei.hui.browser.model.FilExBaseLineTrendVO;
import com.mei.hui.browser.model.FilExGasFeeTrendVO;
import com.mei.hui.browser.service.FilExBaseLineTrendService;
import com.mei.hui.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:56
 **/
@Service
@Slf4j
public class FilExBaseLineTrendServiceImpl extends ServiceImpl<FilExBaseLineTrendMapper,FilExBaseLineTrend>  implements FilExBaseLineTrendService {

    @Autowired
    private FilExBaseLineTrendMapper filExBaseLineTrendMapper;


    /*近30天有效算力走势*/
    @Override
    public List<FilExBaseLineTrendVO> selectThirdDayPower() {
        List<FilExBaseLineTrend> filExBaseLineTrendList = filExBaseLineTrendMapper.selectThirdDayPower();
        List<FilExBaseLineTrendVO> filExBaseLineTrendVOList = filExBaseLineTrendList.stream().map(v->{
            FilExBaseLineTrendVO filExBaseLineTrendVO = new FilExBaseLineTrendVO();
            BeanUtils.copyProperties(v,filExBaseLineTrendVO);
            filExBaseLineTrendVO.setDate(DateUtils.timestampToMMDD(v.getTimestamp()));
            return filExBaseLineTrendVO;
        }).collect(Collectors.toList());
        return filExBaseLineTrendVOList;
    }




}
