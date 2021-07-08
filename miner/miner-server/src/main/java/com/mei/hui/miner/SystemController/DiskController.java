package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.feign.vo.DiskBO;
import com.mei.hui.miner.feign.vo.DiskVO;
import com.mei.hui.miner.service.DiskService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "获取七牛云磁盘相关信息")
@RestController
@RequestMapping("/disk")
public class DiskController {
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "获取磁盘容量信息")
    @PostMapping("/diskSizeInfo")
    public Result<DiskVO> diskSizeInfo(@RequestBody DiskBO diskBO){
        if(StringUtils.isEmpty(diskBO.getMinerId())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不能为空");
        }
        return diskService.diskSizeInfo(diskBO);
    }

}
