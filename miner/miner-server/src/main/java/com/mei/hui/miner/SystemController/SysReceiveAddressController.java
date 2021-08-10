package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.model.SysReceiveAddressBO;
import com.mei.hui.miner.model.SysReceiveAddressVO;
import com.mei.hui.miner.model.UpdateReceiveAddressBO;
import com.mei.hui.miner.service.ISysReceiveAddressService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(value = "收款地址",tags = "收款地址")
@RestController
@RequestMapping("/receiveAddress")
public class SysReceiveAddressController {

    @Autowired
    private ISysReceiveAddressService sysReceiveAddressService;

    @ApiOperation(value = "新增收款地址【鲍红建】")
    @PostMapping("/add")
    public Result addReceiveAddress(@RequestBody SysReceiveAddressBO sysReceiveAddress) {
        //如果是游客不允许修改头像
        if(HttpRequestUtil.isVisitor()){
            throw MyException.fail(ErrorCode.MYB_111005.getCode(),ErrorCode.MYB_111005.getMsg());
        }
        return sysReceiveAddressService.addReceiveAddress(sysReceiveAddress);
    }

    /**
     * 编辑收款地址
     * @description
     * @author shangbin
     * @date 2021/5/14 10:49
     * @return com.mei.hui.util.Result
     * @version v1.0.0
     */
    @ApiOperation(value = "编辑收款地址")
    @PostMapping("/update")
    public Result updateReceiveAddress(@RequestBody UpdateReceiveAddressBO bo) {
        //如果是游客不允许修改头像
        if(HttpRequestUtil.isVisitor()){
            throw MyException.fail(ErrorCode.MYB_111005.getCode(),ErrorCode.MYB_111005.getMsg());
        }
        return sysReceiveAddressService.updateReceiveAddress(bo);
    }

    /**
    * 根据id查询收款地址
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:12
    * @return com.mei.hui.util.Result
    * @version v1.0.0
    */
//    @ApiOperation(value = "根据id查询收款地址")
//    @GetMapping("/{id}")
    public Result<SysReceiveAddressVO> selectSysReceiveAddressById(@PathVariable("id") Long id) {
       return sysReceiveAddressService.selectSysReceiveAddressById(id);
    }

    /**
    * 根据币种id查询收款地址
    *
    * @description
    * @author shangbin
    * @date 2021/5/21 18:50
    * @return com.mei.hui.util.Result<com.mei.hui.miner.model.SysReceiveAddressVO>
    * @version v1.0.0
    */
    @ApiOperation(value = "根据币种id查询收款地址")
    @GetMapping("/selectByCurrencyId")
    public Result<SysReceiveAddressVO> selectSysReceiveAddressByCurrencyId(@RequestParam("currencyId") Long currencyId) {
        return sysReceiveAddressService.selectSysReceiveAddressByCurrencyId(currencyId);
    }


}
