package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillDetail;
import com.mei.hui.miner.feign.vo.FilBillDetailBO;
import com.mei.hui.miner.feign.vo.FilBillDetailVO;
import com.mei.hui.miner.feign.vo.FilBillPageListBO;
import com.mei.hui.miner.feign.vo.FilBillPageListVO;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.service.FilBillDetailService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class FilBillServiceImpl extends ServiceImpl<FilBillMapper, FilBill> implements FilBillService {

    @Autowired
    private FilBillDetailService billDetailService;

    public PageResult<FilBillPageListVO> pageList(FilBillPageListBO bo){
        LambdaQueryWrapper<FilBill> queryWrapper = new LambdaQueryWrapper();
        if(bo.getAccount_type() != null){
            queryWrapper.eq(FilBill::getAccountType,bo.getAccount_type());
        }
        if(StringUtils.isNotEmpty(bo.getMinerId())){
            queryWrapper.eq(FilBill::getMinerId,bo.getMinerId());
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
        queryWrapper.between(FilBill::getDateTime,firstday,lastDay);
        IPage<FilBill> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), queryWrapper);
        List<FilBillPageListVO> list = page.getRecords().stream().map(v -> {
            FilBillPageListVO vo = new FilBillPageListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(),list);
    }


    public Result<List<FilBillDetailVO>> detail(FilBillDetailBO bo){
        if(bo.getBillId() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"账单id不能为空");
        }
        LambdaQueryWrapper<FilBillDetail> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FilBillDetail::getBillId,bo.getBillId());
        List<FilBillDetail> list = billDetailService.list(queryWrapper);

        List<FilBillDetailVO> lt = list.stream().map(v -> {
            FilBillDetailVO vo = new FilBillDetailVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }

}
