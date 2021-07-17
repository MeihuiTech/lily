package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.service.IMinerLongitudeLatitudeService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 矿工节点经纬度
 */
@Api(tags = "矿工节点经纬度")
@RestController
@RequestMapping("/longitudeLatitude")
public class MinerLongitudeLatitudeController {

    @Autowired
    private IMinerLongitudeLatitudeService minerLongitudeLatitudeService;


    @ApiOperation(value = "查询fil矿工id节点地图")
    @GetMapping("/map")
    public Result selectMap(){
        return Result.success(minerLongitudeLatitudeService.selectMap());
    }


}
