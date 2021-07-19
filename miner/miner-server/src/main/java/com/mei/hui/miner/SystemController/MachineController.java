package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.MachineInfoTypeCountVO;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.miner.service.ISysMachineInfoService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "矿机信息")
@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private ISysMachineInfoService machineInfoService;


    /**
     * 查询各种矿机类型的数量
     * @param id
     * @return
     */
    @ApiOperation(value = "查询各种矿机类型的数量")
    @GetMapping(value = "/machineTypeCount/{id}")
    public Result getInfo(@PathVariable("id") Long id){
        MachineInfoTypeCountVO machineInfoTypeCountVO = machineInfoService.selectMachineInfoTypeCountById(id);
        return Result.success(machineInfoTypeCountVO);
    }

}
