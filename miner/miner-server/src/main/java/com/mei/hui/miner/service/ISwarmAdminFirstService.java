package com.mei.hui.miner.service;

import com.mei.hui.miner.feign.vo.SwarmAdminFirstCollectVO;
import com.mei.hui.util.BasePage;

import java.util.Map;

public interface ISwarmAdminFirstService {

    /**
    * swarm管理员首页-平台概览
    *
    * @description
    * @author shangbin
    * @date 2021/5/29 16:59
    * @param []
    * @return com.mei.hui.miner.model.AdminFirstCollectVO
    * @version v1.0.0
    */
    public SwarmAdminFirstCollectVO swarmAdminFirstAllCount();

    /**
     * swarm管理员首页-平台有效出票数排行榜
     *
     * @description
     * @author shangbin
     * @date 2021/5/29 14:12
     * @param [basePage]
     * @return com.mei.hui.util.Result
     * @version v1.0.0
     */
    public Map<String,Object> ticketValidPage(BasePage basePage);


}
