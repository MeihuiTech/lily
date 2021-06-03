package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private IChiaMinerService chiaMinerService;

    @ApiOperation(value = "根据旷工id查询账户按天聚合信息")
    @GetMapping(value = "/{id}/dailyAccount")
    public PageResult dailyAccount(@PathVariable("id") Long id) {
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.dailyAccount(id);
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){//起亚币
            return sysMinerInfoService.chiaDailyAccount(id);
        }
        return null;
    }

    /**
     * 查询矿工信息列表
     */
    @ApiOperation(value = "矿工列表不分页")
    @GetMapping("/listAll")
    public PageResult<SysMinerInfo> listAll(SysMinerInfo sysMinerInfo){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        List<SysMinerInfo> list = null;
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){
            list = sysMinerInfoService.selectSysMinerInfoList(sysMinerInfo);
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){
            list = sysMinerInfoService.findXchMinerList();
        }
        return new PageResult(list.size(),list);
    }


    /**
     * 获取矿工信息详细信息
     */
    @ApiOperation(value = "矿工详情",notes = "chia币矿工详情出参：\n" +
            "\n" +
            "powerAvailable：有效算力, 单位B\n" +
            "totalBlockAward：累计出块奖励,单位XCH\n" +
            "balanceMinerAccount：总资产, 单位XCH\n" +
            "totalBlocks：累计出块份数\n" +
            "powerIncrease：算力增长,单位B")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            SysMinerInfo miner = sysMinerInfoService.selectSysMinerInfoById(id);
            if (miner == null) {
                throw  MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
            }
            return Result.success(miner);
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){//起亚币
            XchMinerDetailBO xchMinerDetailBO = sysMinerInfoService.getXchMinerById(id);
            if (xchMinerDetailBO == null) {
                throw  MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
            }
            return Result.success(xchMinerDetailBO);
        }
        return Result.fail(MinerError.MYB_222222.getCode(),"用户当前币种异常");
    }

    @ApiOperation(value = "根据旷工id查询算力按天聚合信息")
    @GetMapping(value = "/{id}/dailyPower")
    public Map<String,Object> dailyPower(@PathVariable("id") Long id) {
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.dailyPower(id);
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){//起亚币
            return sysMinerInfoService.chiaDailyPower(id);
        }
        return null;
    }

    /**
     * 查询矿工信息列表
     */
    @ApiOperation(value = "矿工列表",notes = "fil矿工列表出参：\n" +
            "\n" +
            "minerId旷工id\n" +
            "balanceMinerAccount挖矿账户余额, 单位FIL\n" +
            "balanceMinerAvailable矿工可用余额,单位FIL\n" +
            "sectorPledge扇区质押, 单位FIL\n" +
            "totalBlockAward累计出块奖励,单位FIL\n" +
            "powerAvailable有效算力, 单位B\n" +
            "machineCount矿机数量\n" +
            "\n" +
            "chia查询矿工信息列表出参：\n" +
            "\n" +
            "minerId旷工id\n" +
            "powerAvailable有效算力, 单位B\n" +
            "totalBlockAward累计出块奖励,单位XCH\n" +
            "balanceMinerAccount总资产, 单位XCH\n" +
            "totalBlocks累计出块份数")
    @GetMapping("/list")
    public Map<String,Object> list(SysMinerInfoBO sysMinerInfoBO){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.findPage(sysMinerInfoBO);
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){//起亚币
            return chiaMinerService.findChiaMinerPage(sysMinerInfoBO);
        }
        return null;
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
