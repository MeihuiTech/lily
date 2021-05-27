package com.mei.hui.miner.service;

import com.mei.hui.miner.model.SysMinerInfoBO;

import java.util.Map;

public interface ChiaMinerService {


    Map<String,Object> findChiaMinerPage(SysMinerInfoBO sysMinerInfoBO);
}
