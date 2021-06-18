package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.PerTicket;
import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.feign.vo.FindChart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import java.util.List;
import java.util.Map;

@Repository
public interface SwarmAggMapper extends BaseMapper<SwarmAgg> {
    /**
     * 根据userId、昨天时间 在聚合统计表里获取昨天的总有效出票数
     * @param userId
     * @param beginYesterdayDate
     * @param endYesterdayDate
     * @return
     */
    public Long selectYesterdayTicketValid(@Param("userId") Long userId, @Param("yesterDayDateYmd") String yesterDayDateYmd);

    List<PerTicket> getPerTicketInfo(Map<String,Object> map);

    IPage<PerTicket> perTicketPageList(Page page, boolean isAsc);

    List<FindChart> findChart(LocalDate startDate,LocalDate endDate, List<String> peerIds);
}