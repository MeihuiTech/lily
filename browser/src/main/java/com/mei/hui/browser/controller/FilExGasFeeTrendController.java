package com.mei.hui.browser.controller;

import com.mei.hui.browser.entity.FilExOverview;
import com.mei.hui.browser.model.FilExGasFeeTrendVO;
import com.mei.hui.browser.model.FilExOverviewVO;
import com.mei.hui.browser.service.FilExGasFeeTrendService;
import com.mei.hui.browser.service.FilExOverviewService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 扇区封装Gas费用
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 16:27
 **/
@Api(tags = "扇区封装Gas费用")
@RestController
@RequestMapping("/filExGasFeeTrend")
@Slf4j
public class FilExGasFeeTrendController {

    @Autowired
    private FilExGasFeeTrendService filExGasFeeTrendService;

    /**
     * 近3小时封装Gas费用(Fil/TiB)
     * @return
     */
    @GetMapping("/thirdGasFee")
    public Result<FilExGasFeeTrendVO> selectThirdGasFeeList(){
        List<FilExGasFeeTrendVO> filExGasFeeTrendVOList = filExGasFeeTrendService.selectThirdGasFeeList();
        return Result.success(filExGasFeeTrendVOList);
    }

}
