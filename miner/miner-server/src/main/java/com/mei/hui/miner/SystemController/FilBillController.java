package com.mei.hui.miner.SystemController;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBillParams;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

@Slf4j
@Api(tags = "fil币账单相关")
@RestController
@RequestMapping("/bill")
public class FilBillController {

    @Autowired
    private FilBillService filBillService;
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;


    @ApiOperation("分页查询日账单列表")
    @PostMapping("/dayAggPage")
    public Result<IPage<FilBillDayAggVO>> selectFilBillDayAggPage(@RequestBody FilBillMonthBO filBillMonthBO){
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId()) && StringUtils.isEmpty(filBillMonthBO.getMonthDate())){
            filBillMonthBO = filBillMonthBOIsNull(filBillMonthBO);
        }
        IPage<FilBillDayAggVO> filBillDayAggVOIPage = filBillService.selectFilBillDayAggPage(filBillMonthBO);

        return Result.success(filBillDayAggVOIPage);
    }

    @ApiOperation("月汇总")
    @PostMapping("/monthAgg")
    public Result<BillTotalVO> selectFilBillmonthAgg(@RequestBody FilBillMonthBO filBillMonthBO){
        BillTotalVO billTotalVO = new BillTotalVO();

        return Result.success(billTotalVO);
    }


    @ApiOperation("日账单详情")
    @PostMapping("/transaction")
    public Result<IPage<FilBillVO>> selectFilBillTransactionsPage(@RequestBody FilBillMonthBO filBillMonthBO){
        IPage<FilBillVO> filBillVOIPage = null;

        return Result.success(filBillVOIPage);
    }


    /**
     * filBillMethodBO为空时设置默认值
     * @param filBillMethodBO
     */
    public FilBillMonthBO filBillMonthBOIsNull(FilBillMonthBO filBillMonthBO){
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoList(new SysMinerInfo());
        log.info("查询矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"该用户没有矿工");
        }
        filBillMonthBO.setMinerId(sysMinerInfoList.get(0).getMinerId());
        filBillMonthBO.setMonthDate(DateUtils.getDate().substring(0,7));
        return filBillMonthBO;
    }





    /**
     * 账单方法下拉列表，20210817废弃，方法保留，以后备用
     * @param filBillMethodBO
     * @return
     */
    @ApiOperation("账单方法下拉列表")
    @PostMapping("/methodList")
    public Result<List<String>> selectFilBillMethodList(@RequestBody FilBillMethodBO filBillMethodBO){
        if (filBillMethodBO == null || StringUtils.isEmpty(filBillMethodBO.getMinerId()) || StringUtils.isEmpty(filBillMethodBO.getSubAccount()) || StringUtils.isEmpty(filBillMethodBO.getMonthDate())){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"入参为空");
        }
        List<String> billMethodList = filBillService.selectFilBillMethodList(filBillMethodBO);
        return Result.success(billMethodList);
    }

    /**
     * 矿工子账户下拉列表，20210817废弃，方法保留，以后备用
     * @param filBillMethodBO
     * @return
     */
    @ApiOperation("矿工子账户下拉列表")
    @PostMapping("/subAccountList")
    public Result<List<FilBillSubAccountVO>> selectFilBillSubAccountList(@RequestBody FilBillMethodBO filBillMethodBO){
        if (filBillMethodBO == null || StringUtils.isEmpty(filBillMethodBO.getMinerId())){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"矿工id为空");
        }
        List<FilBillSubAccountVO> filBillSubAccountVOList = filBillService.selectFilBillSubAccountList(filBillMethodBO);
        return Result.success(filBillSubAccountVOList);
    }

    /**
     * 分页查询账单消息列表，20210817废弃，方法保留，以后备用
     * @param filBillMethodBO
     * @return
     */
    @ApiOperation("分页查询账单消息列表")
    @PostMapping("/page")
    public Result<IPage<FilBillVO>> selectFilBillPage(@RequestBody FilBillMethodBO filBillMethodBO){
        if (StringUtils.isEmpty(filBillMethodBO.getMinerId()) && StringUtils.isEmpty(filBillMethodBO.getSubAccount()) && StringUtils.isEmpty(filBillMethodBO.getMonthDate())){
            filBillMethodBO = filBillMethodBOIsNull(filBillMethodBO);
        }
        IPage<FilBillVO> filBillVOIPage = filBillService.selectFilBillPage(filBillMethodBO);
        return Result.success(filBillVOIPage);
    }

    /**
     * 查询账单汇总信息，20210817废弃，方法保留，以后备用
     * @param filBillMethodBO
     * @return
     */
    @ApiModelProperty("查询账单汇总信息")
    @PostMapping("/total")
    public Result<BillTotalVO> selectFilBillTotal(@RequestBody(required = false) String filBillMethodBO){
        FilBillMethodBO filBillMethodBOEntity = new FilBillMethodBO();
        // 页面初始化的时候这3个字段都不传，后端增加默认值
        if (StringUtils.isNotEmpty(filBillMethodBO)){
            JSONObject jsonObject = JSONObject.parseObject(filBillMethodBO);
            filBillMethodBOEntity = jsonObject.toJavaObject(FilBillMethodBO.class);
            log.info("账单管理入参实体为：【{}】",JSON.toJSON(filBillMethodBOEntity));
        }
        if (StringUtils.isEmpty(filBillMethodBOEntity.getMinerId()) && StringUtils.isEmpty(filBillMethodBOEntity.getSubAccount()) && StringUtils.isEmpty(filBillMethodBOEntity.getMonthDate())){
            filBillMethodBOEntity = filBillMethodBOIsNull(filBillMethodBOEntity);
            log.info("账单管理入参实体设置默认值为：【{}】",JSON.toJSON(filBillMethodBOEntity));
        }
        BillTotalVO billTotalVO = filBillService.selectFilBillTotal(filBillMethodBOEntity);
        return Result.success(billTotalVO);
    }

    /**
     * filBillMethodBO为空时设置默认值
     * @param filBillMethodBO
     */
    public FilBillMethodBO filBillMethodBOIsNull(FilBillMethodBO filBillMethodBO){
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoList(new SysMinerInfo());
        log.info("查询矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"该用户没有矿工");
        }
        filBillMethodBO.setMinerId(sysMinerInfoList.get(0).getMinerId());
        filBillMethodBO.setSubAccount(sysMinerInfoList.get(0).getMinerId());
        filBillMethodBO.setMonthDate(DateUtils.getDate().substring(0,7));
        return filBillMethodBO;
    }

}

