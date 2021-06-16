package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SwarmAgg;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SwarmAggMapper extends BaseMapper<SwarmAgg> {

    List<Map<String,Object>> getPerTicketInfo(Map<String,Object> map);
}