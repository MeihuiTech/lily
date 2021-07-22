package com.mei.hui.miner.SystemController;


import com.mei.hui.miner.feign.vo.FilBillDetailBO;
import com.mei.hui.miner.feign.vo.FilBillDetailVO;
import com.mei.hui.miner.feign.vo.FilBillPageListBO;
import com.mei.hui.miner.feign.vo.FilBillPageListVO;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "fil币账单相关")
@RestController
@RequestMapping("/bill")
public class FilBillController {

    @Autowired
    private FilBillService filBillService;

    @ApiOperation("账单分页列表")
    @PostMapping("/pageList")
    public PageResult<FilBillPageListVO> pageList(@RequestBody FilBillPageListBO bo){
        return filBillService.pageList(bo);
    }

    @ApiOperation("账单明细")
    @PostMapping("/detailList")
    public Result<List<FilBillDetailVO>> detail(@RequestBody FilBillDetailBO bo){
        return filBillService.detail(bo);
    }



}

