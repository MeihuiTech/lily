package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysReceiveAddress;
import com.mei.hui.miner.entity.SysVerifyCode;
import com.mei.hui.miner.model.SysReceiveAddressBO;
import com.mei.hui.miner.model.SysReceiveAddressVO;
import com.mei.hui.miner.model.UpdateReceiveAddressBO;
import com.mei.hui.miner.service.ISysReceiveAddressService;
import com.mei.hui.miner.service.ISysVerifyCodeService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Api(value = "收款地址表",tags = "收款地址表")
@RestController
@RequestMapping("/receiveAddress")
public class SysReceiveAddressController {

    @Autowired
    private ISysReceiveAddressService sysReceiveAddressService;

    @ApiOperation(value = "新增收款地址【鲍红建】")
    @PostMapping("/add")
    public Result addReceiveAddress(@RequestBody SysReceiveAddressBO sysReceiveAddress) {
        return sysReceiveAddressService.addReceiveAddress(sysReceiveAddress);
    }

    /**
     * 编辑收款地址：先逻辑删除原来的，再新建一个新的，保留历史记录
     * @description
     * @author shangbin
     * @date 2021/5/14 10:49
     * @return com.mei.hui.util.Result
     * @version v1.0.0
     */
    @ApiOperation(value = "编辑收款地址")
    @PostMapping("/update")
    public Result updateReceiveAddress(@RequestBody UpdateReceiveAddressBO bo) {
        return sysReceiveAddressService.updateReceiveAddress(bo);
    }

    /**
    * 根据id查询没有被删除的收款地址
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:12
    * @return com.mei.hui.util.Result
    * @version v1.0.0
    */
    @ApiOperation(value = "根据id查询没有被删除的收款地址")
    @GetMapping("/{id}")
    public Result<SysReceiveAddressVO> selectSysReceiveAddressById(@PathVariable("id") Long id) {
       return sysReceiveAddressService.selectSysReceiveAddressById(id);
    }

}
