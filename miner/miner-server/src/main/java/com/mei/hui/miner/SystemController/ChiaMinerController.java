package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/7 14:13
 **/
@Api(value = "起亚币矿工信息",tags = "起亚币矿工信息")
@Slf4j
@RestController
@RequestMapping("/chiaMiner")
public class ChiaMinerController {

    @Autowired
    private IChiaMinerService chiaMinerService;



}
