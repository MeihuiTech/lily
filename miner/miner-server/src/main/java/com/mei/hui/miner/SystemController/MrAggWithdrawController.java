package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.model.AggWithdrawBO;
import com.mei.hui.miner.model.AggWithdrawVO;
import com.mei.hui.miner.service.MrAggWithdrawService;
import com.mei.hui.util.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "用户提币汇总表")
@RequestMapping("/aggWithdraw")
public class MrAggWithdrawController {

    @Autowired
    private MrAggWithdrawService mrAggWithdrawService;
    /**
     * 查询矿工信息列表
     */
    @ApiOperation(value = "用户提币汇总分页【鲍红建】",notes = "入参cloumName排序字段名称:\n" +
            "totalFee手续费总额\n" +
            "tatalCount提取次数\n" +
            "takeTotalMony用户提现总额")
    @GetMapping("/pageList")
    public PageResult<AggWithdrawVO> pageList(AggWithdrawBO aggWithdrawBO){
        return mrAggWithdrawService.pageList(aggWithdrawBO);
    }


}
