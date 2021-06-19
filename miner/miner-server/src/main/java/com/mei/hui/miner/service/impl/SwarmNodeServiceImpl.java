package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.PerTicket;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.entity.SwarmOneDayAgg;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.mapper.SwarmOneDayAggMapper;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private SwarmOneDayAggMapper swarmOneDayAggMapper;


    public PageResult<NodePageListVO> nodePageList(NodePageListBO bo){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        QueryWrapper<NodePageListVO> query = Wrappers.query();
        query.and(Wrapper -> Wrapper.eq("d.date",LocalDate.now().minusDays(1)).or().isNull("d.date"));
        query.eq("n.userId",HttpRequestUtil.getUserId());
        if(StringUtils.isNotEmpty(bo.getIp())){
            query.eq("n.node_ip",bo.getIp());
        }
        if(bo.getState() != null){
            query.eq("n.state",bo.getState());
        }
        if("ticketValid".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc("n.ticket_valid");
            }else{
                query.orderByDesc("n.ticket_valid");
            }
        }
        if("linkNum".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc("n.link_num");
            }else{
                query.orderByDesc("n.link_num");
            }
        }
        if("yestodayTicketValid".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc("d.per_ticket_valid");
            }else{
                query.orderByDesc("d.per_ticket_valid");
            }
        }
        query.orderByDesc("n.create_time");
        log.info("查询节点列表，入参:{}",query.getCustomSqlSegment());
        IPage<NodePageListVO> page = swarmAggMapper.findNodePageList(new Page<>(bo.getPageNum(),bo.getPageSize()), query);
        log.info("查询节点列表，出参:{}",JSON.toJSONString(page.getRecords()));
        return new PageResult(page.getTotal(),page.getRecords());
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
    public Long selectYesterdayTicketValid(String yesterDayDateYmd) {
        return swarmNodeMapper.selectYesterdayTicketValid( yesterDayDateYmd);
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
        LambdaQueryWrapper<SwarmNode> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SwarmNode::getUserid,HttpRequestUtil.getUserId());
        List<SwarmNode> list =this.list(queryWrapper);
        List<FindNodeListVO> lt = list.stream().map(v -> {
            FindNodeListVO vo = new FindNodeListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }





}
