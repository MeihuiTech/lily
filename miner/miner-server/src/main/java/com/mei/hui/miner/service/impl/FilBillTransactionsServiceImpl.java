package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilBillTransactions;
import com.mei.hui.miner.mapper.FilBillTransactionsMapper;
import com.mei.hui.miner.service.FilBillTransactionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/3 17:58
 **/
@Slf4j
@Service
public class FilBillTransactionsServiceImpl extends ServiceImpl<FilBillTransactionsMapper,FilBillTransactions> implements FilBillTransactionsService {
}
