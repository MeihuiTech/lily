package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillDetail;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.service.FilBillDetailService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * FIL币账单 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-21
 */
@Service
@Slf4j
public class FilBillServiceImpl extends ServiceImpl<FilBillMapper, FilBill> implements FilBillService {
    @Autowired
    private FilBillMapper billMapper;

    public Result<BillAggVO> pageList(FilBillPageListBO bo){
        QueryWrapper<FilBill> queryWrapper = new QueryWrapper();
        if(bo.getAccount_type() != null){
            queryWrapper.eq("account_type",bo.getAccount_type());
        }
        if(StringUtils.isNotEmpty(bo.getMinerId())){
            queryWrapper.eq("miner_id",bo.getMinerId());
        }
        if(bo.getType() != null){
            queryWrapper.eq("type",bo.getType());
        }
        if(StringUtils.isNotEmpty(bo.getMethod())){
            queryWrapper.eq("method",bo.getMethod());
        }
        String yearMonth = DateUtils.dateTimeNow(DateUtils.YYYY_MM);
        if(StringUtils.isNotEmpty(bo.getDate())){
            try {
                Date dateTime = DateUtils.dateTime(DateUtils.YYYY_MM, bo.getDate());
                yearMonth = DateUtils.parseDateToStr(DateUtils.YYYY_MM,dateTime);
            } catch (Exception e) {
                throw MyException.fail(MinerError.MYB_222222.getCode(),"时间格式错误");
            }
        }
        DateTimeFormatter fmt = new DateTimeFormatterBuilder().appendPattern("yyyy-MM")
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        LocalDate date = LocalDate.parse(yearMonth, fmt);
        //本月第一天
        LocalDate firstday = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
        //本月的最后一天
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        queryWrapper.between("date_time",firstday,lastDay);
        queryWrapper.orderByDesc("date_time");

        IPage<FilBill> page = billMapper.getBillPageList(new Page<>(bo.getPageNum(), bo.getPageSize()),queryWrapper);
        List<FilBillPageListVO> list = page.getRecords().stream().map(v -> {
            FilBillPageListVO vo = new FilBillPageListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        log.info("数据条数:{}",page.getTotal());

        //转入明细
        BillInVO billInVo = getBillInVO(bo.getMinerId(), bo.getAccount_type(), firstday, lastDay);

        //转出明细
        BillOutVO billOutVo = getBillOutVO(bo.getMinerId(), bo.getAccount_type(), firstday, lastDay);

        BillAggVO vo = new BillAggVO();
        //分页列表数据
        vo.setPage(new PageResult<>(page.getTotal(),list));
        vo.setIn(billInVo);
        vo.setOut(billOutVo);
        return Result.success(vo);
    }

    /**
     * 转出金额统计信息
     * @param minerId 旷工id
     * @param account_type 子账号类型
     * @param firstday 月的第一天时间
     * @param lastDay 月的最后一天时间
     * @return
     */
    public BillOutVO getBillOutVO(String minerId,Integer account_type,LocalDate firstday,LocalDate lastDay){
        QueryWrapper<FilBill> queryWrapper = new QueryWrapper();
        if(account_type != null){
            queryWrapper.eq("account_type",account_type);
        }
        if(StringUtils.isNotEmpty(minerId)){
            queryWrapper.eq("miner_id",minerId);
        }
        queryWrapper.eq("type",0);
        queryWrapper.between("date_time",firstday,lastDay);
        queryWrapper.select("IFNULL(sum(money),0) as total,method");
        queryWrapper.groupBy("method");
        List<Map<String, Object>> list = this.listMaps(queryWrapper);
        BillOutVO vo = new BillOutVO();
        vo.setMap(list);
        log.info("转出金额:{}",JSON.toJSONString(vo));
        return vo;
    }

    /**
     * 获取转入金额统计信息
     * @param minerId 旷工id
     * @param account_type 子账号类型
     * @param firstday 月的第一天时间
     * @param lastDay 月的最后一天时间
     * @return
     */
    public BillInVO getBillInVO(String minerId,Integer account_type,LocalDate firstday,LocalDate lastDay){
        QueryWrapper<FilBill> queryWrapper = new QueryWrapper();
        if(account_type != null){
            queryWrapper.eq("account_type",account_type);
        }
        if(StringUtils.isNotEmpty(minerId)){
            queryWrapper.eq("miner_id",minerId);
        }
        queryWrapper.eq("type",1);
        queryWrapper.between("date_time",firstday,lastDay);
        queryWrapper.select("IFNULL(sum(money),0) as total ");
        Map<String, Object> map = this.getMap(queryWrapper);
        BigDecimal total = new BigDecimal(String.valueOf(map.get("total")));
        BillInVO vo = new BillInVO();
        vo.setTotal(total);
        vo.setOther(total);
        log.info("转入金额:{}",JSON.toJSONString(vo));
        return vo;
    }

}
