package com.mei.hui.miner.SystemController;

import com.alibaba.fastjson.JSON;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.model.ChiaMinerReportedBO;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Api(value="chia客户端上报信息", tags = "chia客户端上报信息")
@RestController
@RequestMapping("/xch/reported")
public class ChiaReportedController
{
    @Autowired
    private IChiaMinerService chiaMinerService;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * chia新增矿工信息
     */
    @ApiOperation(value = "chia新增矿工信息")
    @PostMapping("/miner")
    public Result miner(@RequestBody ChiaMinerReportedBO chiaMinerReportedBO)
    {
        log.info("chia新增矿工信息入参:{}",JSON.toJSON(chiaMinerReportedBO));
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"apiKey不存在");
        }
        Long userId = userIdResult.getData();
        ChiaMiner chiaMiner = new ChiaMiner();
        BeanUtils.copyProperties(chiaMinerReportedBO,chiaMiner);
        chiaMiner.setUserId(userId);

        SysUserOut sysUserInput = new SysUserOut();
        sysUserInput.setUserId(userId);
        Result<SysUserOut> userResult = userFeignClient.getUserById(sysUserInput);
        if (!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())
                || userResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"获取用户失败");
        }
        List<ChiaMiner> chiaMinerList  = chiaMinerService.selectChiaMinerByUserIdAndMinerId(userId, chiaMiner.getMinerId());
        if (chiaMinerList == null || chiaMinerList.size() < 1) {
            chiaMiner.setCreateTime(LocalDateTime.now());
            log.info("新增：【{}】",JSON.toJSON(chiaMiner));
            int rows = chiaMinerService.insertChiaMiner(chiaMiner);
            return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
        } else {
            chiaMiner.setId(chiaMinerList.get(0).getId());
            chiaMiner.setUpdateTime(LocalDateTime.now());
            log.info("修改：【{}】",JSON.toJSON(chiaMiner));
            int rows = chiaMinerService.updateChiaMiner(chiaMiner);
            return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
        }
    }




}