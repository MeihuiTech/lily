package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilBlockAward;
import com.mei.hui.miner.mapper.FilBlockAwardMapper;
import com.mei.hui.miner.service.FilBlockAwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/30 19:40
 **/
@Slf4j
@Service
public class FilBlockAwardServiceImpl extends ServiceImpl<FilBlockAwardMapper,FilBlockAward> implements FilBlockAwardService {

    @Autowired
    private FilBlockAwardMapper filBlockAwardMapper;




}
