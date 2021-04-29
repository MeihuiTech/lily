package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;

/**
 * 矿工信息Controller
 *
 * @author ruoyi
 * @date 2021-03-02
 */
@Api(tags = "矿工信息")
@RestController
@RequestMapping("/system/miner")
public class SysMinerInfoController {

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;
    @Autowired
    private ISysAggAccountDailyService sysAggAccountDailyService;

    @ApiOperation(value = "账户按天聚合信息")
    @GetMapping(value = "/{id}/dailyAccount")
    public PageResult dailyAccount(@PathVariable("id") Long id) {
        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = sysMinerInfoService.selectSysMinerInfoById(id);
        if (miner == null) {
            throw new MyException(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != 1L && !userId.equals(miner.getUserId())) {
            throw new MyException(MinerError.MYB_222222.getCode(),"没有权限");
        }
        Date end = DateUtils.getNowDate();
        Date begin = DateUtils.addDays(end,-29);
        List<SysAggAccountDaily> list = sysAggAccountDailyService.selectSysAggAccountDailyByMinerId(miner.getMinerId(),  DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, begin), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, end));

        PageResult<SysAggAccountDaily> pageResult = new PageResult(list.size(), list);
        return pageResult;
    }

    /**
     * 查询矿工信息列表
     */
    @ApiOperation(value = "矿工列表不分页")
    @GetMapping("/listAll")
    public PageResult<SysMinerInfo> listAll(SysMinerInfo sysMinerInfo)
    {
        Long userId = HttpRequestUtil.getUserId();
        sysMinerInfo.setUserId(userId);
        List<SysMinerInfo> list = sysMinerInfoService.selectSysMinerInfoList(sysMinerInfo);
        return new PageResult(list.size(),list);
    }


    /**
     * 获取矿工信息详细信息
     */
    @ApiOperation(value = "矿工详情")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = sysMinerInfoService.selectSysMinerInfoById(id);
        if (miner == null) {
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != null && 1L == userId && !userId.equals(miner.getUserId())) {
            MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }
        return Result.success(sysMinerInfoService.selectSysMinerInfoById(id));
    }

    @ApiOperation(value = "算力按天聚合信息")
    @GetMapping(value = "/{id}/dailyPower")
    public Result<List<SysAggPowerDaily>> dailyPower(@PathVariable("id") Long id) {
        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = sysMinerInfoService.selectSysMinerInfoById(id);
        if (miner == null) {
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != null && 1L == userId && !userId.equals(miner.getUserId())) {
            MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }
        Date end = DateUtils.getNowDate();
        Date begin = DateUtils.addDays(end,-29);
        List<SysAggPowerDaily> list = sysAggPowerDailyService.selectSysAggAccountDailyByMinerId(miner.getMinerId(),  DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, begin), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, end));
        return Result.success(list);
    }

}
