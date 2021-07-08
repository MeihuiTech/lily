package com.mei.hui.miner.service;

import com.mei.hui.miner.model.AdminFirstCollectVO;
import com.mei.hui.util.BasePage;

import java.util.Map;

public interface IAdminFirstService {

    /**
    * fil管理员首页-矿工统计数据
    *
    * @description
    * @author shangbin
    * @date 2021/5/29 16:59
    * @param []
    * @return com.mei.hui.miner.model.AdminFirstCollectVO
    * @version v1.0.0
    */
    public AdminFirstCollectVO filAdminFirstAllCount();

    /**
    * chia管理员首页-矿工统计数据
    *
    * @description
    * @author shangbin
    * @date 2021/5/29 17:10
    * @param []
    * @return com.mei.hui.miner.model.AdminFirstCollectVO
    * @version v1.0.0
    */
    public AdminFirstCollectVO chiaAdminFirstAllCount();

    /**
     * fil管理员首页-平台有效算力排行榜
     *
     * @description
     * @author shangbin
     * @date 2021/5/29 14:12
     * @param [basePage]
     * @return com.mei.hui.util.Result
     * @version v1.0.0
     */
    public Map<String,Object> filPowerAvailablePage(String yesterDayDate, BasePage basePage);


    /**
     * chia管理员首页-平台有效算力排行榜
     *
     * @description
     * @author shangbin
     * @date 2021/5/29 14:12
     * @param [basePage]
     * @return com.mei.hui.util.Result
     * @version v1.0.0
     */
    public Map<String,Object> chiaPowerAvailablePage(String yesterDayDate, BasePage basePage);



}
