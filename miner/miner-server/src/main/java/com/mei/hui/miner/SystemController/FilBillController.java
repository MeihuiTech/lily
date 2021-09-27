package com.mei.hui.miner.SystemController;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.service.FilBillDayAggService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.*;
import com.mei.hui.util.html.DateFormatEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private FilBillMapper filBillMapper;

    @ApiOperation("分页查询日账单列表")
    @PostMapping("/dayAggPage")
    public Result<IPage<FilBillDayAggVO>> selectFilBillDayAggPage(@RequestBody FilBillMonthBO filBillMonthBO){
        LocalDateTime startDate = filBillMonthBO.getStartMonthDate();
        LocalDateTime endDate = filBillMonthBO.getEndMonthDate();
        if(startDate != null && endDate != null){
            if(startDate.isAfter(endDate)){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"开始时间不能大于结束时间");
            }
        }
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId())){
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
        LocalDateTime startDate = filBillMonthBO.getStartMonthDate();
        LocalDateTime endDate = filBillMonthBO.getEndMonthDate();
        if(startDate != null && endDate != null){
            if(startDate.isAfter(endDate)){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"开始时间不能大于结束时间");
            }
        }
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId())){
            filBillMonthBO = filBillMonthBOIsNull(filBillMonthBO);
        }
        BillTotalVO billTotalVO = filBillService.selectFilBillmonthAgg(filBillMonthBO);

        return Result.success(billTotalVO);
    }

    @ApiOperation("账单总汇总-从矿工创建开始至今所有收入以及支出的汇总")
    @PostMapping("/allAgg")
    public Result<BillTotalVO> selectFilBillAllAgg(@RequestBody(required = false) String filBillMonthBOStr){
        FilBillMonthBO filBillMonthBO = new FilBillMonthBO();
        // 页面初始化的时候这3个字段都不传，后端增加默认值
        if (StringUtils.isNotEmpty(filBillMonthBOStr)){
            JSONObject jsonObject = JSONObject.parseObject(filBillMonthBOStr);
            filBillMonthBO = jsonObject.toJavaObject(FilBillMonthBO.class);
            log.info("账单管理入参实体为：【{}】",JSON.toJSON(filBillMonthBO));
        }
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId())){
            filBillMonthBO = filBillMonthBOIsNull(filBillMonthBO);
        }
        BillTotalVO billTotalVO = filBillService.selectFilBillAllAgg(filBillMonthBO);

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
     */
    public FilBillMonthBO filBillMonthBOIsNull(FilBillMonthBO filBillMonthBO){
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoList(new SysMinerInfo());
        log.info("查询矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"该用户没有矿工");
        }
        filBillMonthBO.setMinerId(sysMinerInfoList.get(0).getMinerId());
        return filBillMonthBO;
    }

    @ApiOperation("根据minerId、月份分页查询月转入、转出、区块奖励列表")
    @PostMapping("/monthTransferPage")
    public Result<IPage<FilBillVO>> selectFilBillMonthTransferPage(@RequestBody FilBillMonthBO filBillMonthBO){
        LocalDateTime startDate = filBillMonthBO.getStartMonthDate();
        LocalDateTime endDate = filBillMonthBO.getEndMonthDate();
        if(startDate != null && endDate != null){
            if(startDate.isAfter(endDate)){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"开始时间不能大于结束时间");
            }
        }
        if (StringUtils.isEmpty(filBillMonthBO.getMinerId())){
            filBillMonthBO = filBillMonthBOIsNull(filBillMonthBO);
        }
        IPage<FilBillVO> filBillVOPage = filBillService.selectFilBillMonthTransferPage(filBillMonthBO);

        return Result.success(filBillVOPage);
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

    @NotAop
    @ApiOperation(value = "账单列表导出excel",produces="application/octet-stream")
    @PostMapping("/export")
    public void export(HttpServletResponse response,@RequestBody ExportBillBO exportBillBO){
        LocalDateTime startDate = exportBillBO.getStartMonthDate();
        LocalDateTime endDate = exportBillBO.getEndMonthDate();
        if(startDate != null && endDate != null){
            if(startDate.isAfter(endDate)){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"开始时间不能大于结束时间");
            }
        }
        LambdaQueryWrapper<FilBillDayAgg> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FilBillDayAgg::getMinerId,exportBillBO.getMinerId());
        if(exportBillBO.getStartMonthDate() != null){
            queryWrapper.ge(FilBillDayAgg::getDate,exportBillBO.getStartMonthDate());
        }
        if(exportBillBO.getEndMonthDate() != null){
            queryWrapper.le(FilBillDayAgg::getDate,exportBillBO.getEndMonthDate());
        }
        List<FilBillDayAgg> filBillDayAggList = filBillDayAggService.list(queryWrapper);
        List<ExportBillVO> list = filBillDayAggList.stream().map(v -> {
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal inMoney = BigDecimal.ZERO;
            BigDecimal outMoney = BigDecimal.ZERO;
            if(v.getBalance().compareTo(new BigDecimal("0")) != 0){
                balance = v.getBalance().divide(new BigDecimal(Math.pow(10, 18)), 9, BigDecimal.ROUND_HALF_UP);
            }
            if(v.getInMoney().compareTo(new BigDecimal("0")) != 0){
                inMoney = v.getInMoney().divide(new BigDecimal(Math.pow(10, 18)), 9, BigDecimal.ROUND_HALF_UP);
            }
            if(v.getOutMoney().compareTo(new BigDecimal("0")) != 0){
                outMoney = v.getOutMoney().divide(new BigDecimal(Math.pow(10, 18)), 9, BigDecimal.ROUND_HALF_UP);
            }
            ExportBillVO vo = new ExportBillVO().setBalance(balance)
                    .setDate(DateUtils.localDateToString(v.getDate(),DateFormatEnum.yyyy_MM_dd)).setInMoney(inMoney)
                    .setOutMoney(outMoney);
            return vo;
        }).collect(Collectors.toList());
        ExcelUtils.export(response, list, "账单信息", ExportBillVO.class);
    }

    @NotAop
    @ApiOperation(value = "导出转入、转出、出块奖励",produces="application/octet-stream")
    @PostMapping("/exportMonthTransfer")
    public void exportMonthTransfer(HttpServletResponse response,@RequestBody ExportMonthTransferBO bo) {
        LocalDateTime startDate = bo.getStartMonthDate();
        LocalDateTime endDate = bo.getEndMonthDate();
        String minerId = bo.getMinerId();
        //资产
        LambdaQueryWrapper<SysMinerInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysMinerInfo::getMinerId,minerId);
        SysMinerInfo sysMinerInfo = sysMinerInfoService.getOne(queryWrapper);
        log.info("矿工信息:{}",JSON.toJSONString(sysMinerInfo));
        SysMinerInfo miner = sysMinerInfoService.selectSysMinerInfoById(sysMinerInfo.getId());
        log.info("矿工资产:{}",JSON.toJSONString(miner));
        //汇总
        FilBillMonthBO filBillMonthBO = new FilBillMonthBO();
        filBillMonthBO.setMinerId(minerId);
        filBillMonthBO.setStartMonthDate(DateUtils.localDateTimeToString(startDate,DateFormatEnum.yyyy_MM));
        filBillMonthBO.setEndMonthDate(DateUtils.localDateTimeToString(endDate,DateFormatEnum.yyyy_MM));
        BillTotalVO filBillDayAgg = filBillService.selectFilBillmonthAgg(filBillMonthBO);
        log.info("汇总:{}",JSON.toJSONString(filBillDayAgg));

        //转入
        List<ExcelFilBill> inList = filBillService.findFilBillMonthTransfer(minerId, startDate, endDate, 0);
        //转出
        List<ExcelFilBill> outList = filBillService.findFilBillMonthTransfer(minerId, startDate, endDate, 1);
        //区块奖励
        List<ExcelFilBill> rewardList = filBillService.findFilBillMonthTransfer(minerId, startDate, endDate, 2);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("明细", "UTF-8") + ".xlsx");
            //获取Excel模块
            ClassPathResource classPathResource = new ClassPathResource("templates/templates.xlsx");
            File file = classPathResource.getFile();
            ExcelWriter excelWriter = EasyExcel.write(out).withTemplate(file).build();

            WriteSheet test1 = EasyExcel.writerSheet(1).build();
            WriteSheet test2 = EasyExcel.writerSheet(2).build();
            WriteSheet test3 = EasyExcel.writerSheet(3).build();
            excelWriter.fill(inList,test1).fill(outList,test2).fill(rewardList,test3);
            excelWriter.finish();
        } catch (IOException e) {
            log.error("异常:",e);
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

