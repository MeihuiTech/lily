package com.mei.hui.browser.Controller;

import com.mei.hui.browser.entity.FilExOverview;
import com.mei.hui.browser.model.FilExOverviewVO;
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
 * 首页全网概览
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 16:27
 **/
@Api(tags = "首页全网概览")
@RestController
@RequestMapping("/filExOverview")
@Slf4j
public class FilExOverviewController {

    @Autowired
    private FilExOverviewService filExOverviewService;

    /**
     * 首页全网概览
     * @return
     */
    @GetMapping("/networkOverview")
    public Result<FilExOverviewVO> selectNetworkOverview(){
        List<FilExOverview> filExOverviewList = filExOverviewService.list();
        if (filExOverviewList != null && filExOverviewList.size() >0){
            FilExOverviewVO filExOverviewVO = new FilExOverviewVO();
            BeanUtils.copyProperties(filExOverviewList.get(0),filExOverviewVO);
            filExOverviewVO.setSixtyFourNewPowerCost(filExOverviewList.get(0).getSixtyFourGas().add(filExOverviewList.get(0).getSectorPledge()));
            filExOverviewVO.setThirtyTwoNewPowerCost(filExOverviewList.get(0).getThirtyTwoGas().add(filExOverviewList.get(0).getSectorPledge()));
            return Result.success(filExOverviewVO);
        } else {
            return Result.success(null);
        }
    }

}
