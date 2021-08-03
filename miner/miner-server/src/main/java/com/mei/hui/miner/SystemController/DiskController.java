package com.mei.hui.miner.SystemController;

import com.alibaba.fastjson.JSON;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
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


    @ApiOperation(value = "获取七牛云集群硬盘容量和宽带信息")
    @GetMapping("/diskSizeAndBroadband")
    public Result<List<QiniuVO>> selectDiskSizeAndBroadbandList(){
        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setUserId(userId);
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoListBySysMinerInfo(sysMinerInfo);
        log.info("不分页根据FIL币矿工信息表entity查询FIL币矿工信息表list出参：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前登录用户不存在矿工");
        }
        return Result.success(diskService.selectDiskSizeAndBroadbandList(sysMinerInfoList));
    }

}
