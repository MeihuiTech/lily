package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.feign.vo.NoPlatformAddBO;
import com.mei.hui.miner.feign.vo.NoPlatformBOPage;
import com.mei.hui.miner.feign.vo.NoPlatformVOPage;
import com.mei.hui.miner.service.NoPlatformMinerService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value="非平台矿工", tags = "非平台矿工")
@RestController
@RequestMapping("/noPlatform")
public class NoPlatformApi {

    @Autowired
    private NoPlatformMinerService noPlatformMinerService;

    @ApiOperation(value = "分页列表")
    @PostMapping("/pageList")
    public PageResult<NoPlatformVOPage> pageList(@RequestBody NoPlatformBOPage bo){
        return noPlatformMinerService.pageList(bo);
    }

    @ApiOperation(value = "新增、编辑")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody NoPlatformAddBO bo){
        return noPlatformMinerService.saveOrUpdate(bo);
    }

    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    public Result delete(@RequestBody NoPlatformAddBO bo){
        if(StringUtils.isEmpty(bo.getMinerId())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不能为空");
        }
        return noPlatformMinerService.delete(bo.getMinerId());
    }
}
