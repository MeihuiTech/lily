package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.common.enums.TransferRecordStatusEnum;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.TakeOutInfoBO;
import com.mei.hui.miner.feign.vo.TakeOutInfoVO;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.mapper.*;
import com.mei.hui.miner.model.*;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.miner.service.FilAdminUserService;
import com.mei.hui.miner.service.ISysTransferRecordService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统划转记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Service
@Slf4j
public class SysTransferRecordServiceImpl implements ISysTransferRecordService {
    @Autowired
    private SysTransferRecordMapper sysTransferRecordMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;
    @Autowired
    private MrAggWithdrawMapper mrAggWithdrawMapper;
    @Autowired
    private ChiaMinerMapper chiaMinerMapper;
    @Autowired
    private SysReceiveAddressMapper sysReceiveAddressMapper;
    @Autowired
    private CurrencyRateService currencyRateService;
    @Autowired
    private FilAdminUserService adminUserService;
    @Autowired
    private UserManager userManager;

    /**
     * 查询系统划转记录
     * @param id 系统划转记录ID
     * @return 系统划转记录
     */
    @Override
    public SysTransferRecord selectSysTransferRecordById(Long id)
    {
        return sysTransferRecordMapper.selectSysTransferRecordById(id);
    }

    /**
     * 查询系统划转记录列表
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录
     */
    @Override
    public List<SysTransferRecord> selectSysTransferRecordList(SysTransferRecord sysTransferRecord)
    {
        return sysTransferRecordMapper.selectSysTransferRecordList(sysTransferRecord);
    }

    /**
     * 新增系统划转记录
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    @Override
    public int insertSysTransferRecord(SysTransferRecord sysTransferRecord)
    {
        sysTransferRecord.setCreateTime(LocalDateTime.now());
        return sysTransferRecordMapper.insertSysTransferRecord(sysTransferRecord);
    }

    /**
     * 修改系统划转记录，如果审核通过，则修改用户收益汇总表
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysTransferRecord(SysTransferRecord sysTransferRecord){
        SysTransferRecord transferRecord = sysTransferRecordMapper.selectById(sysTransferRecord.getId());
        if(transferRecord == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"提交记录不存在");
        }
        if(transferRecord.getStatus() != 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"审核已经完成");
        }
        /**
         * 校验
         */
        LambdaQueryWrapper<SysMinerInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMinerInfo::getMinerId,transferRecord.getMinerId());
        SysMinerInfo miner = sysMinerInfoMapper.selectOne(lambdaQueryWrapper);
        if(miner == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前提取记录异常");
        }
        if(miner.getTotalBlockAward().subtract(miner.getLockAward()).compareTo(sysTransferRecord.getUnLockAward()) < 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"本次解锁奖励错误");
        }
        sysTransferRecord.setUpdateTime(LocalDateTime.now());
        sysTransferRecord.setUnLockAward(sysTransferRecord.getUnLockAward());
        sysTransferRecord.setPrevUnlockAward(sysTransferRecord.getPrevUnlockAward());
        sysTransferRecord.setAmount(sysTransferRecord.getNewAmount());
        sysTransferRecord.setFee(sysTransferRecord.getNewfee());
        /**
         * 修改提现记录状态
         */
        log.info("修改提现记录表状态:{}",JSON.toJSONString(sysTransferRecord));
        int update = sysTransferRecordMapper.updateSysTransferRecord(sysTransferRecord);
        log.info("修改提现记录表状态,结果:{}",update);
        if(update < 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"修改提现记录状态失败");
        }
        /**
         * 如果是提现成功，则修改提现汇总表
         */
        Long userId = transferRecord.getUserId();
        String type = transferRecord.getType();
        Integer pledgeType = transferRecord.getPledgeType();
        if(sysTransferRecord.getStatus() == 1){
            LambdaQueryWrapper<MrAggWithdraw> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MrAggWithdraw::getSysUserId,userId);
            queryWrapper.eq(MrAggWithdraw::getType,type);
            queryWrapper.eq(MrAggWithdraw::getPledgeType,pledgeType);
            log.info("查询提现汇总表记录,userId={}",userId);
            List<MrAggWithdraw> aggWithdraws = mrAggWithdrawMapper.selectList(queryWrapper);
            log.info("查询提现汇总表记录,结果:{}",JSON.toJSONString(aggWithdraws));
            if(aggWithdraws.size() == 0){
                log.info("新增提现汇总信息");
                MrAggWithdraw insertAggWithdraw = MrAggWithdraw.builder().sysUserId(userId).takeTotalMony(transferRecord.getAmount())
                        .type(type).tatalCount(1).totalFee(transferRecord.getFee()).pledgeType(pledgeType).build();
                mrAggWithdrawMapper.insert(insertAggWithdraw);
            }else{
                log.info("更新提现汇总信息");
                MrAggWithdraw mrAggWithdraw = aggWithdraws.get(0);
                mrAggWithdraw.setTakeTotalMony(mrAggWithdraw.getTakeTotalMony().add(transferRecord.getAmount()));
                mrAggWithdraw.setTatalCount(mrAggWithdraw.getTatalCount() + 1);
                mrAggWithdraw.setTotalFee(mrAggWithdraw.getTotalFee().add(transferRecord.getFee()));
                mrAggWithdrawMapper.updateById(mrAggWithdraw);
            }
        }
        return 1;
    }

    /**
     * 批量删除系统划转记录
     * 
     * @param ids 需要删除的系统划转记录ID
     * @return 结果
     */
    @Override
    public int deleteSysTransferRecordByIds(Long[] ids)
    {
        return sysTransferRecordMapper.deleteSysTransferRecordByIds(ids);
    }

    /**
     * 删除系统划转记录信息
     * 
     * @param id 系统划转记录ID
     * @return 结果
     */
    @Override
    public int deleteSysTransferRecordById(Long id)
    {
        return sysTransferRecordMapper.deleteSysTransferRecordById(id);
    }

    /**
     * 获取用户已提取收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public Double selectTotalWithdrawByUserId(Long userId)
    {
        return sysTransferRecordMapper.selectTotalWithdrawByUserId(userId);
    }

    /**
     * 总手续费收益
     * @return
     */
    @Override
    public List<TransferRecordFeeVO> selectTotalEarning(List<Long> userIds) {
        return sysTransferRecordMapper.selectTotalEarning(userIds);
    }

    /**
     * 今日手续费收益
     * @return
     */
    @Override
    public List<TransferRecordFeeVO> selectTodayEarning(Date todayBeginDate,List<Long> userIds) {
        return sysTransferRecordMapper.selectTodayEarning(todayBeginDate,userIds);
    }

    @Override
    public Map<String,Object> findTransferRecords(SysTransferRecord sysTransferRecord) {
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if (StringUtils.isEmpty(sysTransferRecord.getMinerId())) {
            throw MyException.fail(MinerError.MYB_222222.getCode(), "minerId不能为空值");
        }
        LambdaQueryWrapper<SysTransferRecord> queryWrapper = new LambdaQueryWrapper<>();
        if("createTime".equals(sysTransferRecord.getCloumName())){
            if(sysTransferRecord.isAsc()){
                queryWrapper.orderByAsc(SysTransferRecord::getCreateTime);
            }else{
                queryWrapper.orderByDesc(SysTransferRecord::getCreateTime);
            }
        }else if("amount".equals(sysTransferRecord.getCloumName())){
            if(sysTransferRecord.isAsc()){
                queryWrapper.orderByAsc(SysTransferRecord::getAmount);
            }else{
                queryWrapper.orderByDesc(SysTransferRecord::getAmount);
            }
        }else if("fee".equals(sysTransferRecord.getCloumName())){
            if(sysTransferRecord.isAsc()){
                queryWrapper.orderByAsc(SysTransferRecord::getFee);
            }else{
                queryWrapper.orderByDesc(SysTransferRecord::getFee);
            }
        }else{
            queryWrapper.orderByDesc(SysTransferRecord::getCreateTime);
        }
        queryWrapper.eq(SysTransferRecord::getUserId,HttpRequestUtil.getUserId());
        queryWrapper.eq(SysTransferRecord::getMinerId,sysTransferRecord.getMinerId());
        queryWrapper.eq(SysTransferRecord::getType,CurrencyEnum.getCurrency(currencyId).name());
        IPage<SysTransferRecord> page = sysTransferRecordMapper.selectPage(new Page<>(sysTransferRecord.getPageNum(), sysTransferRecord.getPageSize()), queryWrapper);
        //批量获取用户
        List<Long> userids = page.getRecords().stream().map(v -> {
            return v.getUserId();
        }).collect(Collectors.toList());
        Map<Long, SysUserOut> userMaps = sysUserToMap(userids);

        page.getRecords().stream().forEach(v -> {
            SysUserOut user = userMaps.get(v.getUserId());
            v.setUserName(user.getUserName());
            v.setAmount(BigDecimalUtil.formatFour(v.getAmount()));
            v.setFee(BigDecimalUtil.formatFour(v.getFee()));
        });
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows", page.getRecords());
        map.put("total", page.getTotal());
        return map;
    }

    public Map<String,Object> DataNull(){
        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg", ErrorCode.MYB_000000.getMsg());
        result.put("rows", new ArrayList());
        result.put("total", 0);
        return result;
    }
    /**
     * 查询系统划转记录列表,加UserName
     *系统划转记录
     * @return 系统划转记录集合
     */
    @Override
    @Transactional
    public Map<String,Object> selectSysTransferRecordListUserName(AggWithdrawBO aggWithdrawBO){
        //查询当前管理员负责管理的普通用户
        List<Long> userIds = adminUserService.findUserIdsByAdmin();

        LambdaQueryWrapper<SysTransferRecord> queryWrapper = new LambdaQueryWrapper<>();
        if("amount".equals(aggWithdrawBO.getCloumName())){
            if(aggWithdrawBO.isAsc()){
                queryWrapper.orderByAsc(SysTransferRecord::getAmount);
            }else {
                queryWrapper.orderByDesc(SysTransferRecord::getAmount);
            }
        } else if("fee".equals(aggWithdrawBO.getCloumName())){
            if(aggWithdrawBO.isAsc()){
                queryWrapper.orderByAsc(SysTransferRecord::getFee);
            }else {
                queryWrapper.orderByDesc(SysTransferRecord::getFee);
            }
        } else {
            queryWrapper.orderByDesc(SysTransferRecord::getCreateTime);
        }
        //用于入参模块模糊查询，获取用户id
        String userName = aggWithdrawBO.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
            FindSysUsersByNameBO bo = new FindSysUsersByNameBO();
            bo.setName(userName);
            log.info("模糊查询用户id集合：【{}】",JSON.toJSON(bo));
            Result<List<FindSysUsersByNameVO>> userResult = userFeignClient.findSysUsersByName(bo);
            log.info("模糊查询用户id集合结果:{}", JSON.toJSONString(userResult));
            if(!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())){
                throw MyException.fail(userResult.getCode(),userResult.getMsg());
            }
            if(userResult.getData().size() == 0){
                return DataNull();
            }
            List<Long> idList = userResult.getData().stream().map(v ->v.getUserId()).collect(Collectors.toList());
            userIds = userIds.stream().filter(item -> idList.contains(item)).collect(Collectors.toList());
        }
        if(userIds.size() ==0){
            return DataNull();
        }
        queryWrapper.in(SysTransferRecord::getUserId,userIds);
        // 查询条件币种
        Long currencyId = aggWithdrawBO.getCurrencyId();
        if (currencyId != null) {
            CurrencyEnum currencyEnum = CurrencyEnum.getCurrency(currencyId);
            if (currencyEnum == null) {
                throw MyException.fail(MinerError.MYB_222222.getCode(),"入参币种不存在");
            }
            String currencyType = currencyEnum.name();
            queryWrapper.eq(SysTransferRecord::getType,currencyType);
        }
        if (aggWithdrawBO.getStatus() != null){
            queryWrapper.eq(SysTransferRecord::getStatus,aggWithdrawBO.getStatus());
        }
        log.info("根据 entity 条件，查询全部记录（并翻页）入参：【{}】",JSON.toJSON(queryWrapper));
        IPage<SysTransferRecord> page = sysTransferRecordMapper.selectPage(new Page<>(aggWithdrawBO.getPageNum(), aggWithdrawBO.getPageSize()), queryWrapper);
        List<Long> ids = page.getRecords().stream().map(v -> v.getUserId()).collect(Collectors.toList());
        /**
         * 查询用户
         */
        Map<Long,SysUserOut> map = new HashMap<>();
        if(ids.size() > 0){
            FindSysUserListInput findSysUserListInput = new FindSysUserListInput();
            findSysUserListInput.setUserIds(ids);
            log.info("批量获取用户入参：【{}】",JSON.toJSON(findSysUserListInput));
            Result<List<SysUserOut>> users = userFeignClient.findSysUserList(findSysUserListInput);
            log.info("批量获取用户出参：【{}】",JSON.toJSON(users));
            users.getData().stream().forEach(v->map.put(v.getUserId(),v));
        }
        //组装返回信息
        if(map.size() > 0){
            page.getRecords().stream().forEach(v->{
                        v.setUserName(map.get(v.getUserId()).getUserName());
                        v.setAmount(BigDecimalUtil.formatFour(v.getAmount()));
                        v.setFee(BigDecimalUtil.formatFour(v.getFee()));
                    });
        }
        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg", ErrorCode.MYB_000000.getMsg());
        result.put("rows", page.getRecords());
        result.put("total", page.getTotal());
        return result;
    }

    public Map<Long,SysUserOut>  sysUserToMap(List<Long> userids){
        FindSysUserListInput input = new FindSysUserListInput();
        input.setUserIds(userids);
        log.info("请求用户模块");
        Result<List<SysUserOut>> result = userFeignClient.findSysUserList(input);
        log.info("用户模块返回值:{}", JSON.toJSONString(result));
        Map<Long,SysUserOut> map = new HashMap<>();
        if(ErrorCode.MYB_000000.getCode().equals(result.getCode())){
            List<SysUserOut> users = result.getData();
            users.stream().forEach(v->{
                map.put(v.getUserId(),v);
            });
        }
        return map;
    }


    /**
     * 用户提币：
     * 1、先校验现有余额是否 大于 将要提取的fil, 余额 - 带提币中的fil > 即将提取的fil
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result withdraw(SysTransferRecordWrap sysTransferRecordWrap){
        Long userId = HttpRequestUtil.getUserId();
        Long currencyId = HttpRequestUtil.getCurrencyId();
        String currencyType = CurrencyEnum.getCurrency(currencyId).name();
        String minerId = sysTransferRecordWrap.getMinerId();

        /**
         * 一：提取金额 < 可提现金额 - 提币中 金额
         */
        LambdaQueryWrapper<SysMinerInfo> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysMinerInfo::getMinerId,minerId);
        wrapper.eq(SysMinerInfo::getUserId,userId);
        SysMinerInfo sysMinerInfo = sysMinerInfoMapper.selectOne(wrapper);
        log.info("查询fil矿工信息,出参:{}",JSON.toJSONString(sysMinerInfo));
        if(sysMinerInfo == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工不存在");
        }
        sysTransferRecordWrap.setFee(sysTransferRecordWrap.getFee());
        //实际到账金额 = 提币金额 - 平台佣金
        sysTransferRecordWrap.setAmount(sysTransferRecordWrap.getAmount());

        // 只有提取类型为0提取收益时，才验证提币地址是否正确
        // 通过用户ID、货币ID查询用户货币地址表中的地址
        if (Constants.PLEDGETYPEZERO.equals(sysTransferRecordWrap.getPledgeType())){
            QueryWrapper<SysReceiveAddress> sysReceiveAddressQueryWrapper = new QueryWrapper<>();
            SysReceiveAddress sysReceiveAddress = new SysReceiveAddress();
            sysReceiveAddress.setUserId(userId);
            sysReceiveAddress.setCurrencyId(currencyId);
            sysReceiveAddressQueryWrapper.setEntity(sysReceiveAddress);
            List<SysReceiveAddress> sysReceiveAddressList = sysReceiveAddressMapper.selectList(sysReceiveAddressQueryWrapper);
            if (sysReceiveAddressList == null || sysReceiveAddressList.size() < 1) {
                throw MyException.fail(MinerError.MYB_222222.getCode(),"提币地址不存在");
            }
            String address = sysReceiveAddressList.get(0).getAddress();
            log.info("sysTransferRecordWrap.getToAddress():【{}】,address:【{}】",sysTransferRecordWrap.getToAddress(),address);
            if (!sysTransferRecordWrap.getToAddress().equals(address)){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"提币地址错误");
            }
        }

        //记录提币申请
        SysTransferRecord sysTransferRecord = new SysTransferRecord();
        BeanUtils.copyProperties(sysTransferRecordWrap, sysTransferRecord);
        sysTransferRecord.setUserId(userId);
        sysTransferRecord.setCreateTime(LocalDateTime.now());
        sysTransferRecord.setUpdateTime(LocalDateTime.now());
        sysTransferRecord.setStatus(0);
        sysTransferRecord.setMinerId(sysTransferRecordWrap.getMinerId());
        sysTransferRecord.setCreateTime(LocalDateTime.now());
        sysTransferRecord.setType(currencyType);
        log.info("记录提币申请：【{}】", JSON.toJSON(sysTransferRecord));
        int rows = sysTransferRecordMapper.insert(sysTransferRecord);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
     * 提币，获取到账金额、平台佣金
     * @param takeOutInfoBO
     * @return
     */
    public Result<TakeOutInfoVO> takeOutInfo(TakeOutInfoBO takeOutInfoBO){
        /**
         * 获取矿工基础信息
         */
        LambdaQueryWrapper<SysMinerInfo> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysMinerInfo::getMinerId,takeOutInfoBO.getMinerId());
        SysMinerInfo sysMinerInfo = sysMinerInfoMapper.selectOne(wrapper);
        log.info("矿工信息:{}",JSON.toJSONString(sysMinerInfo));
        if(sysMinerInfo == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"minerId 错误");
        }
        if(sysMinerInfo.getUserId() != HttpRequestUtil.getUserId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"没有提现权限");
        }
        /**
         * 获取上次提取时的解锁奖励
         */
        //上次已解锁奖励
        BigDecimal prevUnlockAward = getPrevUnlockAward(takeOutInfoBO.getMinerId());

        //本次实结已解锁奖励 = 累计出块奖励 - 锁仓收益 - 上次提现解锁奖励
        BigDecimal takeOutMoney = sysMinerInfo.getTotalBlockAward().subtract(sysMinerInfo.getLockAward()).subtract(prevUnlockAward);
        log.info("本次实结已解锁奖励:{}",takeOutMoney);
        /**
         * 获取费率
         */
        Map<String, BigDecimal> rateMap = currencyRateService.getUserRateMap(sysMinerInfo.getUserId());
        BigDecimal feeRate = rateMap.get(CurrencyEnum.FIL.name());//费率
        log.info("费率:{}",feeRate);

        BigDecimal fee = feeRate.multiply(takeOutMoney).divide(new BigDecimal(100));
        BigDecimal arriveMoney = takeOutMoney.subtract(fee);
        TakeOutInfoVO takeOutInfoVO = new TakeOutInfoVO()
                .setArriveMoney(BigDecimalUtil.formatFour(arriveMoney))
                .setFee(BigDecimalUtil.formatFour(fee));
        return Result.success(takeOutInfoVO);
    }

    @Override
    public Result<GetTransferRecordByIdVO> getTransferRecordById(GetTransferRecordByIdBO transferRecordByIdBO){
        GetTransferRecordByIdVO getTransferRecordByIdVO = new GetTransferRecordByIdVO();
        SysTransferRecord transferRecord = sysTransferRecordMapper.selectById(transferRecordByIdBO.getId());
        log.info("系统划转记录:{}",JSON.toJSONString(transferRecord));
        if(transferRecord == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"ID错误");
        }
        BeanUtils.copyProperties(transferRecord,getTransferRecordByIdVO);

        //查询矿工的出块总奖励、总锁仓收益
        LambdaQueryWrapper<SysMinerInfo> minerQuery = new LambdaQueryWrapper<>();
        minerQuery.eq(SysMinerInfo::getMinerId,transferRecord.getMinerId());
        SysMinerInfo miner = sysMinerInfoMapper.selectOne(minerQuery);
        log.info("矿工数据:{}",JSON.toJSONString(miner));
        getTransferRecordByIdVO.setTotalBlockAward(BigDecimalUtil.formatFour(miner.getTotalBlockAward()));
        getTransferRecordByIdVO.setLockAward( BigDecimalUtil.formatFour(miner.getLockAward()));
        //获取用户姓名
        SysUserOut user = userManager.getUserById(transferRecord.getUserId());
        getTransferRecordByIdVO.setUserName(user.getUserName());

        //上次解锁收益:如果划转记录是“审核中”则通过getPrevUnlockAward获取；否则，获取划转记录中的 prev_unlock_award 字段
        BigDecimal unLockAward = miner.getTotalBlockAward().subtract(miner.getLockAward());
        BigDecimal prevUnlockAward = getPrevUnlockAward(transferRecord.getMinerId());
        if(transferRecord.getStatus() != 0){
            prevUnlockAward = transferRecord.getPrevUnlockAward();
            unLockAward = transferRecord.getUnLockAward();
        }
        //计算解锁奖励
        getTransferRecordByIdVO.setUnLockAward(BigDecimalUtil.formatFour(unLockAward));
        getTransferRecordByIdVO.setPrevUnlockAward(BigDecimalUtil.formatFour(prevUnlockAward));
        /**
         * 计算最近的可结算金额
         */
        //获取费率
        Map<String, BigDecimal> rateMap = currencyRateService.getUserRateMap(miner.getUserId());
        BigDecimal feeRate = rateMap.get(CurrencyEnum.FIL.name());//费率
        log.info("费率:{}",feeRate);
        if(feeRate.compareTo(BigDecimal.ZERO) > 0){
            getTransferRecordByIdVO.setFeeRate(feeRate.divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP));
        }

        /**
         * 只在审核完后，查看的时候需要显示
         */
        //本次实结已解锁奖励 = 累计出块奖励 - 锁仓收益 - 上次提现解锁奖励
        BigDecimal takeOutMoney = unLockAward.subtract(prevUnlockAward);
        log.info("本次实结已解锁奖励:{}",takeOutMoney);
        BigDecimal fee = feeRate.multiply(takeOutMoney).divide(new BigDecimal(100));
        BigDecimal realMoney = takeOutMoney.subtract(fee);
        getTransferRecordByIdVO.setNewAmount(BigDecimalUtil.formatFour(realMoney));
        getTransferRecordByIdVO.setNewFee(BigDecimalUtil.formatFour(fee));
        getTransferRecordByIdVO.setRealMoney(BigDecimalUtil.formatFour(takeOutMoney));

        return Result.success(getTransferRecordByIdVO);
    }

    /**
     * 获取上次解锁收益
     * @param minerId
     * @return
     */
    public BigDecimal getPrevUnlockAward(String minerId){
        BigDecimal prevUnlockAward = BigDecimal.ZERO;
        LambdaQueryWrapper<SysTransferRecord> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysTransferRecord::getMinerId,minerId);
        queryWrapper.eq(SysTransferRecord::getStatus,1);
        queryWrapper.orderByDesc(SysTransferRecord::getUpdateTime).last("limit 1");
        List<SysTransferRecord> list = sysTransferRecordMapper.selectList(queryWrapper);
        log.info("获取最后一条提取记录:{}",JSON.toJSONString(list));
        if(list.size() != 0){
            SysTransferRecord  prevTransferRecord = list.get(0);
            prevUnlockAward = prevTransferRecord.getUnLockAward();
        }
        return prevUnlockAward;
    }

    /**
     * 查询用户收益fil
     * @param input
     * @return
     */
    @Override
    public Result getUserEarning(GetUserEarningInput input){
        Long userId = HttpRequestUtil.getUserId();
        String minerId = input.getMinerId();
        if(StringUtils.isEmpty(minerId)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"minerId 为空");
        }
        EarningVo earningVo = new EarningVo(0.0, 0.0, 0.0, 0.0);
        /**
         *获取矿工信息
         */
        log.info("查询矿工信息,userId ={},minerId={}",userId,minerId);
        LambdaQueryWrapper<SysMinerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMinerInfo::getMinerId,minerId);
        queryWrapper.eq(SysMinerInfo::getUserId,userId);
        List<SysMinerInfo> miners = sysMinerInfoMapper.selectList(queryWrapper);
        log.info("矿工信息查询结果:{}",JSON.toJSONString(miners));
        if(miners == null || miners.size() == 0){
            return Result.success(earningVo);
        }
        SysMinerInfo miner = miners.get(0);
        /**
         * 获取提币成功状态的提取记录
         */
        LambdaQueryWrapper<SysTransferRecord> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SysTransferRecord::getStatus,1);//提币成功
        lambdaQueryWrapper.eq(SysTransferRecord::getUserId,userId);
        lambdaQueryWrapper.eq(SysTransferRecord::getMinerId,minerId);
        lambdaQueryWrapper.eq(SysTransferRecord::getType,CurrencyEnum.FIL.name());
        log.info("查询提币完成的记录");
        List<SysTransferRecord> transfers = sysTransferRecordMapper.selectList(lambdaQueryWrapper);
        log.info("提币完成的记录查询结果:{}",JSON.toJSONString(transfers));
        //已提取收益
        BigDecimal totalWithdraw = BigDecimal.ZERO;
        for(SysTransferRecord record : transfers ) {
            totalWithdraw = totalWithdraw.add(record.getAmount()).add(record.getFee());
        }
        earningVo.setTotalWithdraw(BigDecimalUtil.formatFour(totalWithdraw).doubleValue());
        /**
         * 获取所有可提取币
         */
        earningVo.setTotalEarning(BigDecimalUtil.formatFour(miner.getTotalBlockAward()).doubleValue());
        earningVo.setTotalLockAward(BigDecimalUtil.formatFour(miner.getLockAward()).doubleValue());

        //添加 正在提币中的数量
        LambdaQueryWrapper<SysTransferRecord> drawingWrapper = new LambdaQueryWrapper();
        drawingWrapper.eq(SysTransferRecord::getStatus,0);//提币中
        drawingWrapper.eq(SysTransferRecord::getUserId,userId);
        drawingWrapper.eq(SysTransferRecord::getMinerId,minerId);
        lambdaQueryWrapper.eq(SysTransferRecord::getType,CurrencyEnum.FIL.name());
        log.info("查询提币中的记录");
        List<SysTransferRecord> transferRecords = sysTransferRecordMapper.selectList(drawingWrapper);
        log.info("提币中的记录查询结果:{}",JSON.toJSONString(transfers));
        BigDecimal drawing = BigDecimal.ZERO;
        for(SysTransferRecord record : transferRecords ) {
            drawing = drawing.add(record.getAmount()).add(record.getFee());
        }
        earningVo.setDrawingEarning(BigDecimalUtil.formatFour(drawing).doubleValue());

        /**
         * 获取上次提取时的解锁奖励
         */
        //上次已解锁奖励
        BigDecimal prevUnlockAward = getPrevUnlockAward(minerId);
        //本次实结已解锁奖励 = 累计出块奖励 - 锁仓收益 - 上次提现解锁奖励
        BigDecimal takeOutMoney = miner.getTotalBlockAward().subtract(miner.getLockAward()).subtract(prevUnlockAward);
        log.info("本次实结已解锁奖励:{}",takeOutMoney);
        earningVo.setAvailableEarning(BigDecimalUtil.formatFour(takeOutMoney).doubleValue());

        return Result.success(earningVo);
    }
    /**
     * 查询用户收益chia
     * @param input
     * @return
     */
    @Override
    public Result getUserChiaEarning(GetUserEarningInput input){
        Long userId = HttpRequestUtil.getUserId();
        String minerId = input.getMinerId();
        if(StringUtils.isEmpty(minerId)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"minerId 为空");
        }
        EarningChiaVo earningVo = new EarningChiaVo(0.0, 0.0);
        /**
         *获取矿工信息
         */
        log.info("查询矿工信息,userId ={},minerId={}",userId,minerId);
        LambdaQueryWrapper<ChiaMiner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChiaMiner::getMinerId,minerId);
        queryWrapper.eq(ChiaMiner::getUserId,userId);
        List<ChiaMiner> miners = chiaMinerMapper.selectList(queryWrapper);
        log.info("矿工信息查询结果:{}",JSON.toJSONString(miners));
        if(miners == null || miners.size() == 0){
            return Result.success(earningVo);
        }
        ChiaMiner miner = miners.get(0);
        earningVo.setAvailableEarning(BigDecimalUtil.formatFour(miner.getBalanceMinerAccount()).doubleValue());
        /**
         * 获取已提取收益
         */
        LambdaQueryWrapper<SysTransferRecord> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.ne(SysTransferRecord::getStatus,2);//提币成功
        lambdaQueryWrapper.eq(SysTransferRecord::getUserId,userId);
        lambdaQueryWrapper.eq(SysTransferRecord::getMinerId,minerId);
        lambdaQueryWrapper.eq(SysTransferRecord::getType, CurrencyEnum.XCH.name());
        log.info("查询提币完成的记录");
        List<SysTransferRecord> transfers = sysTransferRecordMapper.selectList(lambdaQueryWrapper);
        log.info("提币完成的记录查询结果:{}",JSON.toJSONString(transfers));
        BigDecimal totalWithdraw = BigDecimal.ZERO;//已提取的收益
        BigDecimal drawing = BigDecimal.ZERO;//提取中的收益
        for(SysTransferRecord record : transfers ) {
            if(record.getStatus() == TransferRecordStatusEnum.FINISH.getStatus()){
                totalWithdraw = totalWithdraw.add(record.getAmount()).add(record.getFee());
            }else if(record.getStatus() == TransferRecordStatusEnum.DRAWING.getStatus()){
                drawing = drawing.add(record.getAmount()).add(record.getFee());
            }
        }
        earningVo.setTotalWithdraw(BigDecimalUtil.formatFour(totalWithdraw).doubleValue());
        earningVo.setDrawingEarning(BigDecimalUtil.formatFour(drawing).doubleValue());
        return Result.success(earningVo);
    }
}
