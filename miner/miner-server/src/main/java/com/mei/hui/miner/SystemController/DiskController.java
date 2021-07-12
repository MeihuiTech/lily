package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.BroadbandVO;
import com.mei.hui.miner.feign.vo.DiskBO;
import com.mei.hui.miner.feign.vo.DiskVO;
import com.mei.hui.miner.service.DiskService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "获取七牛云磁盘相关信息")
@RestController
@RequestMapping("/disk")
public class DiskController {
    @Autowired
    private DiskService diskService;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @ApiOperation(value = "获取磁盘容量信息")
    @PostMapping("/diskSizeInfo")
    public Result<DiskVO> diskSizeInfo(@RequestBody DiskBO diskBO){
        String minerId = diskBO.getMinerId();
        if(StringUtils.isEmpty(minerId)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不能为空");
        }

        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setMinerId(minerId);
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoListBySysMinerInfo(sysMinerInfo);
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不存在");
        }
        if (Constants.STORETYPEQINIU.equals(sysMinerInfoList.get(0).getStoreType())) {
            return diskService.diskSizeInfo(diskBO);
        } else {
            return Result.OK;
        }
    }


    @ApiOperation(value = "获取宽带信息")
    @PostMapping("/broadband")
    public Result<BroadbandVO> broadband(@RequestBody DiskBO diskBO){
        String minerId = diskBO.getMinerId();
        if(StringUtils.isEmpty(minerId)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不能为空");
        }

        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setMinerId(minerId);
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoListBySysMinerInfo(sysMinerInfo);
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不存在");
        }
        if (Constants.STORETYPEQINIU.equals(sysMinerInfoList.get(0).getStoreType())) {
            return diskService.broadband(diskBO);
        } else {
            return Result.OK;
        }
    }

}
