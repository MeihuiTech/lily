package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilDeadlines;
import com.mei.hui.miner.feign.vo.ReportDeadlinesBO;
import com.mei.hui.util.Result;

/**
 * <p>
 * filcoin 矿工窗口记录表 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
public interface FilDeadlinesService extends IService<FilDeadlines> {
    Result reportDeadlines(ReportDeadlinesBO bo);

    /**
    * 用户首页WindowPoSt的96个窗口
    *
    * @description
    * @author shangbin
    * @date 2021/6/25 16:38
    * @param []
    * @return com.mei.hui.util.Result
    * @version v1.4.0
    */
    public Result selectFilDeadlinesNinetySixList(String minerId);
}
