package com.mei.hui.miner.service;

import com.mei.hui.miner.feign.vo.FindDiskSizeInfoBO;
import com.mei.hui.util.Result;

import java.util.List;

public interface GeneralService {
    Result<List<FindDiskSizeInfoBO>> findDiskSizeInfo();
}
