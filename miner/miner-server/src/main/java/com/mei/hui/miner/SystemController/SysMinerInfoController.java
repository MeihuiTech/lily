package com.mei.hui.miner.SystemController;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.miner.service.FilAdminUserService;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
@Slf4j
public class SysMinerInfoController {
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private IChiaMinerService chiaMinerService;
    @Autowired
    private FilAdminUserService adminUserService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RuoYiConfig ruoYiConfig;
    @Autowired
    private UserManager userManager;

    @ApiOperation(value = "根据矿工id查询账户按天聚合信息")
    @GetMapping(value = "/{id}/dailyAccount")
    public PageResult dailyAccount(@PathVariable("id") Long id) {
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.dailyAccount(id);
        }else if(CurrencyEnum.XCH.getCurrencyId() == currencyId){//起亚币
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
        }else if(CurrencyEnum.XCH.getCurrencyId() == currencyId){
            list = sysMinerInfoService.findXchMinerList();
        }
        return new PageResult(list.size(),list);
    }


    /**
     * 普通用户-首页-获取矿工信息详细信息
     */
    @ApiOperation(value = "矿工详情",notes = "FIL币矿工详情出参：\n" +
            "balanceWorkerAccount：" +
            "所有在线矿机数量allOnlineMachineCount\n" +
            "所有离线矿机数量allOfflineMachineCount\n" +
            "Miner在线矿机数量minerOnlineMachineCount\n" +
            "Miner离线矿机数量minerOfflineMachineCount\n" +
            "post在线矿机数量postOnlineMachineCount\n" +
            "post离线矿机数量postOfflineMachineCount\n" +
            "c2在线矿机数量ctwoOnlineMachineCount\n" +
            "c2离线矿机数量ctwoOfflineMachineCount\n" +
            "seal在线矿机数量sealOnlineMachineCount\n" +
            "seal离线矿机数量sealOfflineMachineCount\n" +
            "\n" +
            "chia币矿工详情出参：\n" +
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
        }else if(CurrencyEnum.XCH.getCurrencyId() == currencyId){//起亚币
            XchMinerDetailBO xchMinerDetailBO = sysMinerInfoService.getXchMinerById(id);
            if (xchMinerDetailBO == null) {
                throw  MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
            }
            return Result.success(xchMinerDetailBO);
        }
        return Result.fail(MinerError.MYB_222222.getCode(),"用户当前币种异常");
    }

    @ApiOperation(value = "根据矿工id查询算力按天聚合信息")
    @GetMapping(value = "/{id}/dailyPower")
    public Map<String,Object> dailyPower(@PathVariable("id") Long id) {
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.dailyPower(id);
        }else if(CurrencyEnum.XCH.getCurrencyId() == currencyId){//起亚币
            return sysMinerInfoService.chiaDailyPower(id);
        }
        return null;
    }

    /**
     * 查询矿工信息列表
     */
    @ApiOperation(value = "矿工列表",notes = "fil矿工列表出参：\n" +
            "\n" +
            "minerId矿工id\n" +
            "balanceMinerAccount挖矿账户余额, 单位FIL\n" +
            "balanceMinerAvailable矿工可用余额,单位FIL\n" +
            "sectorPledge扇区质押, 单位FIL\n" +
            "totalBlockAward累计出块奖励,单位FIL\n" +
            "powerAvailable有效算力, 单位B\n" +
            "machineCount矿机数量\n" +
            "powerIncreasePerDay算力增速, 单位B\n" +
            "blocksPerDay当天出块份数\n" +
            "balanceWorkerAccount：worker账户余额\n" +
            "sectorAvailable有效状态扇区数量\n" +
            "sectorError错误状态扇区数量\n" +
            "\n" +
            "chia查询矿工信息列表出参：\n" +
            "\n" +
            "minerId矿工id\n" +
            "powerAvailable有效算力, 单位B\n" +
            "totalBlockAward累计出块奖励,单位XCH\n" +
            "balanceMinerAccount总资产, 单位XCH\n" +
            "totalBlocks累计出块份数")
    @GetMapping("/list")
    public Map<String,Object> list(SysMinerInfoBO sysMinerInfoBO){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.findPage(sysMinerInfoBO);
        }else if(CurrencyEnum.XCH.getCurrencyId() == currencyId){//起亚币
            return chiaMinerService.findChiaMinerPage(sysMinerInfoBO);
        }
        return null;
    }

    /**
     * 获取当前矿工的矿机列表
     */
    @ApiOperation(value = "矿机列表")
    @GetMapping(value = "machines/{id}")
    public Map<String,Object> machines(@PathVariable("id") Long id,int pageNum,int pageSize,Integer online,String machineType) {
        return sysMinerInfoService.machines(id,pageNum,pageSize,online,machineType);
    }

    @ApiOperation(value = "管理员-用户收益-分页查询用户收益列表",notes = "管理员-用户收益-用户收益列表出参：\n" +
            "userId用户Id\n" +
            "userName用户名\n" +
            "powerAvailable总算力\n" +
            "totalBlockAward总收益\n" +
            "feeRate费率")
    @GetMapping("/selectUserMoneyList")
    public PageResult<FilUserMoneyVO> selectUserMoneyList(FilUserMoneyBO filUserMoneyBO){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){//fil 币
            return sysMinerInfoService.selectUserMoneyList(filUserMoneyBO);
        }else if(CurrencyEnum.XCH.getCurrencyId().equals(currencyId)){//起亚币
            return chiaMinerService.selectUserMoneyList(filUserMoneyBO);
        }
        return null;
    }

    /**
     * 管理员页面-平台概览-矿工列表
     * @param sysMinerInfoBO
     * @return
     */
    @ApiOperation("管理员页面-平台概览-矿工列表")
    @GetMapping("/admin/minerPagelist")
    public Map<String,Object> minerPagelist(SysMinerInfoBO sysMinerInfoBO){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return sysMinerInfoService.minerPagelist(sysMinerInfoBO);
        }else if(CurrencyEnum.XCH.getCurrencyId() == currencyId){//起亚币
            return chiaMinerService.findChiaMinerPage(sysMinerInfoBO);
        }
        return null;
    }

    /**
     * 获取
     * @return
     */
    @ApiOperation("分配用户-获取用户、矿工")
    @PostMapping("/findAllMiner")
    public Result<List<FindAllMinerVO>> findAllMiner(){
        return sysMinerInfoService.findAllMiner();
    }

    @ApiOperation("分配用户-分页列表")
    @PostMapping("/adminUserPage")
    public PageResult<AdminUserPageBO> adminUserPage(@RequestBody BasePage basePage){
        return adminUserService.adminUserPage(basePage);
    }

    @ApiOperation("分配用户-编辑")
    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(@RequestBody UpdateAdminUserBO bo){
        return adminUserService.saveOrUpdateAdmin(bo);
    }

    @NotAop
    @ApiOperation("矿工列表导出excel")
    @GetMapping("/exportMinerInfoExcel")
    public void exportMinerInfoExcel(HttpServletResponse response){
        ExcelWriter writer = null;
        OutputStream out = null;
        try {
            List<MinerInfoExportExcelVO> minerInfoExportExcelVOList = sysMinerInfoService.exportMinerInfoExcel();
            out = response.getOutputStream();
            writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            String fileName = "矿工列表表格";
            Sheet sheet = new Sheet(1, 0, MinerInfoExportExcelVO.class);
            sheet.setSheetName("矿工列表");
            writer.write(minerInfoExportExcelVOList, sheet);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "ISO8859-1"));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.finish();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取
     * @return
     */
    @ApiOperation("给游客设置userId,获取用户列表")
    @PostMapping("/findAllUserAndMiner")
    public Result<AllUserAndMinerBO> findAllUserAndMiner(){
        Result<List<FindAllMinerVO>> allUserAndMiner = sysMinerInfoService.findAllMiner();
        List<FindAllMinerVO> users = allUserAndMiner.getData();
        log.info("用户和拥有的矿工:{}", JSON.toJSONString(users));
        AllUserAndMinerBO bo = new AllUserAndMinerBO().setUsers(users);
        /**
         * 如果没有缓存游客的登陆userId，则从配置用取
         */
        String visitorUserId = redisUtil.get(Constants.visitorKey);
        log.info("游客用户id:{}",visitorUserId);
        if(StringUtils.isNotEmpty(visitorUserId)){
            SysUserOut user = userManager.getUserById(Long.valueOf(visitorUserId));
            bo.setVisitorUserId(user.getUserId()).setUserName(user.getUserName());
        }
        return Result.success(bo);
    }

    /**
     * 获取
     * @return
     */
    @ApiOperation("设置游客登陆使用的userId")
    @PostMapping("/setVisitorUserId")
    public Result setVisitorUserId(@RequestBody SetVisitorUserIdBO bo){
        return sysMinerInfoService.setVisitorUserId(bo);
    }

}
