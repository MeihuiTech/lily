package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.AggMiner;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 矿工信息Controller
 *
 * @author ruoyi
 * @date 2021-03-02
 */
@Api(tags = "矿工信息")
@RestController
@RequestMapping("/system/miner")
public class SysMinerInfoController<ISysMachineInfoService> {

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

        list.stream().forEach(v->{
            v.setBalanceAccount(BigDecimalUtil.formatFour(v.getBalanceAccount()));
        });
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
    public Map<String,Object> dailyPower(@PathVariable("id") Long id) {
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
        Map<String,Object> map = new HashMap<>();
        map.put("code",ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",list);
        map.put("total",list.size());
        return map;
    }

    /**
     * 查询矿工信息列表
     */
    @ApiOperation(value = "矿工列表")
    @GetMapping("/list")
    public Map<String,Object> list(SysMinerInfo sysMinerInfo)
    {
        return sysMinerInfoService.findPage(sysMinerInfo);
    }

    /**
     * 获取当前矿工的矿机列表
     */
    @ApiOperation(value = "矿机列表")
    @GetMapping(value = "machines/{id}")
    public Map<String,Object> machines(@PathVariable("id") Long id,int pageNum,int pageSize) {
        return sysMinerInfoService.machines(id,pageNum,pageSize);
    }

    /**
     * 通过userid 集合批量获取旷工
     */
    @ApiOperation(value = "通过userid 集合批量获取旷工")
    @GetMapping(value = "/findBatchMinerByUserId")
    public Result<List<AggMinerVO>> findBatchMinerByUserId(@RequestParam("userIds") List<Long> userIds) {
        return sysMinerInfoService.findBatchMinerByUserId(userIds);
    }

}
