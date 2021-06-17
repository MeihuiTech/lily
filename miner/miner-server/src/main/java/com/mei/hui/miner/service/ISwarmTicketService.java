package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.SwarmTicket;
import com.mei.hui.miner.feign.vo.TicketPageListBO;
import com.mei.hui.miner.feign.vo.TicketPageListVO;
import com.mei.hui.util.PageResult;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:15
 **/
public interface ISwarmTicketService extends IService<SwarmTicket> {

    PageResult<TicketPageListVO> ticketPageList(TicketPageListBO bo);
}
