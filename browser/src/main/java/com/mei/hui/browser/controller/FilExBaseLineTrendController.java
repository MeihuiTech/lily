package com.mei.hui.browser.controller;

import com.mei.hui.browser.model.FilExBaseLineTrendVO;
import com.mei.hui.browser.service.FilExBaseLineTrendService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 近30天有效算力走势
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 16:27
 **/
@Api(tags = "近30天有效算力走势")
@RestController
@RequestMapping("/filExBaseLineTrend")
@Slf4j
public class FilExBaseLineTrendController {

    @Autowired
    private FilExBaseLineTrendService filExBaseLineTrendService;

    /**
     * 近30天有效算力走势
     * @return
     */
    @GetMapping("/thirdDayPower")
    public Result<List<FilExBaseLineTrendVO>> selectThirdDayPower(){
        List<FilExBaseLineTrendVO> filExBaseLineTrendVOList = filExBaseLineTrendService.selectThirdDayPower();
        return Result.success(filExBaseLineTrendVOList);
    }

}
