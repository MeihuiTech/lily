package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.model.ListCurrencyBO;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.miner.service.ISysCurrencyService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 币种表
 * @author shangbin
 * @version v1.0.0
 * @date
 **/
@Slf4j
@Api(value = "币种",tags = "币种")
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private ISysCurrencyService sysCurrencyService;
    /**
    * @description不分页排序查询币种列表
    * @param
    */
    @ApiOperation(value = "不分页排序查询币种列表")
    @GetMapping("/list")
    public Result<List<SysCurrencyVO>> listCurrency(){
        List<SysCurrencyVO> sysCurrencyVOList = sysCurrencyService.listCurrency();
        ListCurrencyBO listCurrencyBO = new ListCurrencyBO();
        listCurrencyBO.setList(sysCurrencyVOList);
        listCurrencyBO.setCurrencyId(HttpRequestUtil.getCurrencyId());
        return Result.success(sysCurrencyVOList);
    }

}
