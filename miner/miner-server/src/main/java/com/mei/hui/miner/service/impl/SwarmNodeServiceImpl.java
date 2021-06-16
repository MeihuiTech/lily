package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:16
 **/
@Service
@Slf4j
public class SwarmNodeServiceImpl extends ServiceImpl<SwarmNodeMapper, SwarmNode> implements ISwarmNodeService {
}
