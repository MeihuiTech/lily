package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.AdminFirstService;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminFirstServiceImpl implements AdminFirstService {

    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;

    /**
     * 管理员 fil币 首页接口
     * @return
     */
    public Result filFirst(){




        return null;
    }
}
