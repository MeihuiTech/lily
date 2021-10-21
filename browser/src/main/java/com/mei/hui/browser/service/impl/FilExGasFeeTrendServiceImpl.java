package com.mei.hui.browser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.browser.entity.FilExGasFeeTrend;
import com.mei.hui.browser.mapper.FilExGasFeeTrendMapper;
import com.mei.hui.browser.model.FilExGasFeeTrendVO;
import com.mei.hui.browser.service.FilExGasFeeTrendService;
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
public class FilExGasFeeTrendServiceImpl extends ServiceImpl<FilExGasFeeTrendMapper,FilExGasFeeTrend>  implements FilExGasFeeTrendService {

    @Autowired
    private FilExGasFeeTrendMapper filExGasFeeTrendMapper;

    /*近3小时封装Gas费用(Fil/TiB)*/
    @Override
    public List<FilExGasFeeTrendVO> selectThirdGasFeeList() {
        Long beforeThirdSecond = DateUtils.lDTBeforeOrAfterHourTimestamp(-3);
        List<FilExGasFeeTrend> filExGasFeeTrendList = filExGasFeeTrendMapper.selectThirdGasFeeList(beforeThirdSecond);
        List<FilExGasFeeTrendVO> filExGasFeeTrendVOList = filExGasFeeTrendList.stream().map(v->{
            FilExGasFeeTrendVO filExGasFeeTrendVO = new FilExGasFeeTrendVO();
            BeanUtils.copyProperties(v,filExGasFeeTrendVO);
            filExGasFeeTrendVO.setTime(DateUtils.timestampToHHmmss(v.getTimestamp()));
            return filExGasFeeTrendVO;
        }).collect(Collectors.toList());
        return filExGasFeeTrendVOList;
    }

}
