package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.PerTicket;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:16
 **/
@Service
@Slf4j
public class SwarmNodeServiceImpl extends ServiceImpl<SwarmNodeMapper, SwarmNode> implements ISwarmNodeService {

    @Autowired
    private SwarmAggMapper swarmAggMapper;

    @Autowired
    private SwarmNodeMapper swarmNodeMapper;

    @Autowired
    private UserFeignClient userFeignClient;


    public PageResult<NodePageListVO> nodePageList(NodePageListBO bo){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        LambdaQueryWrapper<SwarmNode> query = new LambdaQueryWrapper();
        if(StringUtils.isNotEmpty(bo.getIp())){
            query.eq(SwarmNode::getNodeIp,bo.getIp());
        }
        if(bo.getState() != null){
            query.eq(SwarmNode::getState,bo.getState());
        }
        if("ticketValid".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc(SwarmNode::getTicketAvail);
            }else{
                query.orderByDesc(SwarmNode::getTicketAvail);
            }
        }
        if("linkNum".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc(SwarmNode::getLinkNum);
            }else{
                query.orderByDesc(SwarmNode::getLinkNum);
            }
        }
        //先在聚合表统计节点和昨日出票数，然后进行排序后返回
        if("yestodayTicketValid".equalsIgnoreCase(bo.getCloumName())){
            List<String> peerIds = perTicketPageList(bo);
            if(peerIds != null && peerIds.size() > 0){
                query.in(SwarmNode::getPeerId,peerIds);
            }
        }
        query.orderByDesc(SwarmNode::getCreateTime);
        log.info("查询节点列表，入参:{}",query.getCustomSqlSegment());
        IPage<SwarmNode> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), query);
        log.info("查询节点列表，出参:{}",JSON.toJSONString(page.getRecords()));
        List<NodePageListVO> list = page.getRecords().stream().map(v -> {
            NodePageListVO vo = new NodePageListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());

        log.info("获取节点的每日出票信息");
        putYestodayTicketValid(list);

        return new PageResult(page.getTotal(),list);
    }

    /**
     * 按昨日出票数排序后返回节点地址，即节点的唯一标识
     * @param bo
     * @return
     */
    public List<String> perTicketPageList(NodePageListBO bo){
        log.info("按昨日出票进行排序,入参:isAsc={}",bo.isAsc());
        IPage<PerTicket> page = swarmAggMapper.perTicketPageList(new Page<>(bo.getPageNum(), bo.getPageSize()), bo.isAsc());
        log.info("按昨日出票进行排序,出参:{}",JSON.toJSONString(page.getRecords()));
        List<String> list = page.getRecords().stream().map(v -> v.getPeerId()).collect(Collectors.toList());
        log.info("按昨日出票数排序列表:{}",JSON.toJSONString(list));
        return list;
    }

    /**
     * 组装昨日有效出票数
     * @param list
     */
    public void putYestodayTicketValid(List<NodePageListVO> list){
        if(list.size() ==0){
            return;
        }
        List<String> peerIds = list.stream().map(v ->v.getPeerId()).collect(Collectors.toList());
        Map<String,Object> param = new HashMap<>();
        param.put("peerIds",peerIds);
        param.put("startDate",LocalDate.now().minusDays(1));
        param.put("endDate",LocalDate.now());
        log.info("查询节点每天的出票数，入参:{}", JSON.toJSONString(param));
        List<PerTicket> perTicketInfos = swarmAggMapper.getPerTicketInfo(param);
        log.info("查询节点每天的出票数，出参:{}", JSON.toJSONString(perTicketInfos));
        if(perTicketInfos == null || perTicketInfos.size() == 0){
            return;
        }
        Map<String, PerTicket> perTickets = new HashMap<>();
        perTicketInfos.stream().forEach(v->{
            String key = v.getPeerId();
            perTickets.put(key,v);
        });
        list.stream().forEach(v->{
            PerTicket perTicket = perTickets.get(v.getPeerId());
            if(perTicket != null){
                v.setYestodayTicketValid(perTicket.getTotalPerTicketValid());
                v.setYestodayTicketAvail(perTicket.getTotalPerTicketAvail());
            }
        });
    }

    /**
     * 管理员首页-平台概览-总有效出票数，用的字段：有效出票数
     * @return
     */
    @Override
    public Long selectTicketValid() {
        return swarmNodeMapper.selectTicketValid();
    }

    /**
     * 管理员首页-平台概览-昨日有效出票份数
     * @return
     */
    @Override
    public Long selectYesterdayTicketValid(Date beginYesterdayDate, Date endYesterdayDate) {
        return swarmNodeMapper.selectYesterdayTicketValid(beginYesterdayDate, endYesterdayDate);
    }

    /**
     * 管理员首页-平台概览-有效节点
     * @return
     */
    @Override
    public Long selectNodeValid() {
        return swarmNodeMapper.selectNodeValid();
    }

    /**
     * 管理员首页-平台概览-平台总连接数
     * @return
     */
    @Override
    public Long selectLinkNum() {
        return swarmNodeMapper.selectLinkNum();
    }

    /**
     * 管理员首页-平台有效出票数排行榜
     * @param swarmTicketValidVOPage
     * @param ticketValid
     * @return
     */
    @Override
    public IPage<SwarmTicketValidVO> ticketValidPage(Page<SwarmTicketValidVO> swarmTicketValidVOPage, Long ticketValid) {
        return swarmNodeMapper.ticketValidPage(swarmTicketValidVOPage, ticketValid);
    }

    /**
     * 管理员-用户收益-多条件分页查询用户列表
     * @param swarmUserMoneyBO
     * @return
     */
    @Override
    public PageResult<SwarmUserMoneyVO> selectUserMoneyList(SwarmUserMoneyBO swarmUserMoneyBO) {
        //用于入参模块模糊查询，获取用户id的list
        String userName = swarmUserMoneyBO.getUserName();
        List<Long> userIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userName)) {
            FindSysUsersByNameBO bo = new FindSysUsersByNameBO();
            bo.setName(userName);
            log.info("模糊查询用户id集合");
            Result<List<FindSysUsersByNameVO>> userResult = userFeignClient.findSysUsersByName(bo);
            log.info("模糊查询用户id集合结果:{}", JSON.toJSONString(userResult));
            List<Long> idList = new ArrayList<>();
            if(ErrorCode.MYB_000000.getCode().equals(userResult.getCode()) && userResult.getData().size() > 0){
                idList = userResult.getData().stream().map(v ->v.getUserId()).collect(Collectors.toList());
                // id去重
                Set<Long> idsSet = new HashSet<>(idList);
                userIdList = new ArrayList<Long>(idsSet);
            } else {
                return new PageResult(0,new ArrayList());
            }
        }

        Page<SwarmUserMoneyVO> page = new Page<SwarmUserMoneyVO>(swarmUserMoneyBO.getPageNum(),swarmUserMoneyBO.getPageSize());
        log.info("多条件分页查询用户列表入参page：【{}】,swarmUserMoneyBO：【{}】,userIdList：【{}】",JSON.toJSON(page),JSON.toJSON(swarmUserMoneyBO),userIdList);
        IPage<SwarmUserMoneyVO> result = swarmNodeMapper.selectUserMoneyList(page,swarmUserMoneyBO.getUserId(),swarmUserMoneyBO.getCloumName(),swarmUserMoneyBO.isAsc(),userIdList);
        log.info("多条件分页查询用户列表出参：【{}】",JSON.toJSON(result));
        if (result == null){
            return new PageResult(0,new ArrayList());
        }
        result.getRecords().stream().forEach(v -> {
            v.setMoney(BigDecimalUtil.formatEight(v.getMoney()));

            SysUserOut sysUserOut = new SysUserOut();
            sysUserOut.setUserId(v.getUserId());
            log.info("查询用户姓名入参：【{}】",JSON.toJSON(sysUserOut));
            Result<SysUserOut> sysUserOutResult = userFeignClient.getUserById(sysUserOut);
            log.info("查询用户姓名出参：【{}】",JSON.toJSON(sysUserOutResult));
            if(ErrorCode.MYB_000000.getCode().equals(sysUserOutResult.getCode())){
                v.setUserName(sysUserOutResult.getData().getUserName());
            }
        });
        PageResult pageResult = new PageResult(result.getTotal(),result.getRecords());
        return pageResult;
    }

    /**
     * 获取节点ip列表
     * @return
     */
    public Result<List<FindNodeListVO>> findNodeList(){
        List<SwarmNode> list = this.list();
        List<FindNodeListVO> lt = list.stream().map(v -> {
            FindNodeListVO vo = new FindNodeListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }





}
