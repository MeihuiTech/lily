package com.mei.hui.miner.SystemController;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.entity.FilBillParams;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.service.FilBillDayAggService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private FilBillDayAggService filBillDayAggService;


    @ApiOperation("分页查询日账单列表")
    @PostMapping("/dayAggPage")
    public Result<IPage<FilBillDayAggVO>> selectFilBillDayAggPage(@RequestBody FilBillMonthBO filBillMonthBO){
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId()) && StringUtils.isEmpty(filBillMonthBO.getMonthDate())){
            filBillMonthBO = filBillMonthBOIsNull(filBillMonthBO);
        }
        IPage<FilBillDayAggVO> filBillDayAggVOIPage = filBillService.selectFilBillDayAggPage(filBillMonthBO);

        return Result.success(filBillDayAggVOIPage);
    }

    @ApiOperation("账单月汇总")
    @PostMapping("/monthAgg")
    public Result<BillTotalVO> selectFilBillmonthAgg(@RequestBody(required = false) String filBillMonthBOStr){
        FilBillMonthBO filBillMonthBO = new FilBillMonthBO();
        // 页面初始化的时候这3个字段都不传，后端增加默认值
        if (StringUtils.isNotEmpty(filBillMonthBOStr)){
            JSONObject jsonObject = JSONObject.parseObject(filBillMonthBOStr);
            filBillMonthBO = jsonObject.toJavaObject(FilBillMonthBO.class);
            log.info("账单管理入参实体为：【{}】",JSON.toJSON(filBillMonthBO));
        }
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId()) && StringUtils.isEmpty(filBillMonthBO.getMonthDate())){
            filBillMonthBO = filBillMonthBOIsNull(filBillMonthBO);
        }
        BillTotalVO billTotalVO = filBillService.selectFilBillmonthAgg(filBillMonthBO);

        return Result.success(billTotalVO);
    }


    @ApiOperation("分页查询日账单详情列表")
    @PostMapping("/transaction")
    public Result<IPage<FilBillVO>> selectFilBillTransactionsPage(@RequestBody FilBillMonthBO filBillMonthBO){
        IPage<FilBillVO> filBillVOIPage = filBillService.selectFilBillTransactionsPage(filBillMonthBO);
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

    @ApiOperation("手动调用fil币账单按天聚合定时器")
    @PostMapping("/ManualInsertFilBillDayAggTask")
    public Result<Integer> ManualInsertFilBillDayAggTask(@RequestBody FilBillMonthBO filBillMonthBO){
        String monthDateStr = filBillMonthBO.getMonthDate();
        String[] dateArr = monthDateStr.split(",");
        Integer insertCountAll = 0;
        for (int i= 0;i<dateArr.length;i++){
            List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.list();
            log.info("查询所有矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoList));
            log.info("矿工数量：【{}】",sysMinerInfoList.size());

            // 下面代码是测试用的，不要放开注释
            String startDate = dateArr[i] + " 00:00:00";
            String endDate = dateArr[i] + " 23:59:59";
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateArr[i], dateTimeFormatter);

            for (SysMinerInfo sysMinerInfo:sysMinerInfoList){
                String minerId = sysMinerInfo.getMinerId();
                try {
                    QueryWrapper<FilBillDayAgg> queryWrapper = new QueryWrapper<>();
                    FilBillDayAgg filBillDayAgg = new FilBillDayAgg();
                    filBillDayAgg.setMinerId(minerId);
                    filBillDayAgg.setDate(date);
                    queryWrapper.setEntity(filBillDayAgg);
                    List<FilBillDayAgg> filBillDayAggList = filBillDayAggService.list(queryWrapper);
                    log.info("根据矿工minerId：【{}】，日期Date：【{}】查询FIL币账单消息每天汇总表出参：【{}】",minerId,filBillDayAgg.getDate(),JSON.toJSON(filBillDayAggList));
                    if (filBillDayAggList != null && filBillDayAggList.size() > 0){
                        log.info("该矿工minerId：【{}】，日期Date：【{}】数据已存在，跳过",minerId,filBillDayAgg.getDate());
                        continue;
                    }

                    log.info("新增FIL币账单消息每天汇总表入参minerId：【{}】，startDate：【{}】，endDate：【{}】，date：【{}】",minerId,startDate,endDate,date);
                    Integer insertCount = filBillService.insertFilBillDayAgg(minerId,startDate,endDate,date);
                    insertCountAll += insertCount;
                } catch (Exception e){
                    log.info("新增FIL币账单消息每天汇总表异常：minerId：【{}】，startDate：【{}】，endDate：【{}】，date：【{}】",minerId,startDate,endDate,date);
                    log.info("异常信息：",e);
                }
            }
        }

        return Result.success(insertCountAll);
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

