package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.DiskBO;
import com.mei.hui.miner.feign.vo.DiskVO;
import com.mei.hui.miner.service.DiskService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "获取七牛云磁盘相关信息")
@RestController
@RequestMapping("/currency")
public class DiskController {
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "获取磁盘容量信息")
    @GetMapping("/diskSizeInfo")
    public Result<DiskVO> diskSizeInfo(@RequestBody DiskBO diskBO){
        return diskService.diskSizeInfo(diskBO);
    }

}
