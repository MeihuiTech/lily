package com.mei.hui.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.feign.feignClient.AggChiaMinerFeign;
import com.mei.hui.miner.feign.feignClient.AggMinerFeignClient;
import com.mei.hui.miner.feign.feignClient.CurrencyRateFeign;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysLogininfor;
import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.entity.SysUserRole;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.user.manager.FeeRateManager;
import com.mei.hui.user.mapper.SysLogininforMapper;
import com.mei.hui.user.mapper.SysRoleMapper;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.mapper.SysUserRoleMapper;
import com.mei.hui.user.model.*;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.*;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private RuoYiConfig ruoYiConfig;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    protected SysUserMapper sysUserMapper;
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private AggMinerFeignClient aggMinerFeignClient;
    @Autowired
    private AggChiaMinerFeign aggChiaMinerFeign;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysLogininforMapper sysLogininforMapper;
    @Autowired
    private FeeRateManager feeRateManager;

    @Autowired
    private CurrencyRateFeign currencyRateFeign;

    public Map<String,Object> getSysUserByNameAndPass(LoginBody loginBody){

        /**
         * 验证码校验
         */
        String verifyKey = Constants.CAPTCHA_CODE_KEY + loginBody.getUuid();
        String captcha = redisUtils.get(verifyKey);
        redisUtils.delete(verifyKey);
        if (captcha == null){
            throw  MyException.fail(UserError.MYB_333333.getCode(),"验证码错误");
        }
        if (!loginBody.getCode().equalsIgnoreCase(captcha)){
            throw  MyException.fail(UserError.MYB_333333.getCode(),"验证码错误");
        }
        /**
         * 用户校验
         */
        String passWord = AESUtil.encrypt(loginBody.getPassword());
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName,loginBody.getUsername());
        queryWrapper.eq(SysUser::getPassword,passWord);
        queryWrapper.eq(SysUser::getStatus,0);
        queryWrapper.eq(SysUser::getDelFlag,0);
        List<SysUser> sysUsers = sysUserMapper.selectList(queryWrapper);
        if(sysUsers.size() == 0){
            throw new MyException(ErrorCode.MYB_111111.getCode(),"用户和或密码错误");
        }
        SysUser sysUser = sysUsers.get(0);
        Map<String,Object> result = new HashMap<>();
        result.put("code",ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());

        //默认当前币种时fil币
        Long currencyId = Constants.fileCurrencyId;
        //生成token
        String token = JwtUtil.createToken(sysUser.getUserId(),currencyId,Constants.WEB);
        result.put(SystemConstants.TOKEN,token);
        redisUtils.set(token,currencyId+"",ruoYiConfig.getJwtMinutes(),TimeUnit.MINUTES);
        insertLoginInfo(sysUser);
        return result;
    }

    public void insertLoginInfo(SysUser sysUser){
        try {
            HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
            final UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
            // 获取客户端操作系统
            String os = userAgent.getOperatingSystem().getName();
            // 获取客户端浏览器
            String browser = userAgent.getBrowser().getName();
            final String ip = IpUtils.getIpAddr(httpServletRequest);
            SysLogininfor logininfor = new SysLogininfor();
            logininfor.setUserName(sysUser.getUserName());
            logininfor.setIpaddr(ip);
            logininfor.setBrowser(browser);
            logininfor.setOs(os);
            sysLogininforMapper.insertLogininfor(logininfor);
        } catch (Exception e) {
            log.info("登陆日志插入失败");
        }
    }

    /**
     * 查询用户所属角色组
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysRole role : list)
        {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString()))
        {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public SysUser getUserById(Long userId){
        SysUser sysUser = sysUserMapper.selectById(userId);
        return sysUser;
    }

    /**
     * 获取当前登陆用户
     * @return
     */
    public SysUser getLoginUser(){
        Long userId = HttpRequestUtil.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        return sysUser;
    }

    public Result<List<SysUserOut>> findSysUserList(FindSysUserListInput req){
        if(req == null || req.getUserIds() == null || req.getUserIds().size() == 0){
            throw MyException.fail(UserError.MYB_333333.getCode(),"id集合为空");
        }
        List<SysUser> list = sysUserMapper.selectBatchIds(req.getUserIds());
        List<SysUserOut> users = list.stream().map(v -> {
            SysUserOut sysUserOut = new SysUserOut();
            BeanUtils.copyProperties(v, sysUserOut);
            return sysUserOut;
        }).collect(Collectors.toList());
        return Result.success(users);
    }

    public Result<List<SysUserOut>> findAllUser(){
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDelFlag,0);
        queryWrapper.eq(SysUser::getStatus,0);
        List<SysUser> list = sysUserMapper.selectList(queryWrapper);
        List<SysUserOut> users = list.stream().map(v -> {
            SysUserOut sysUserOut = new SysUserOut();
            BeanUtils.copyProperties(v, sysUserOut);
            return sysUserOut;
        }).collect(Collectors.toList());
        return Result.success(users);
    }

    /**
     * 用户模糊查询
     * @param req
     * @return
     */
    public Result<List<FindSysUsersByNameVO>> findSysUsersByName(FindSysUsersByNameBO req){
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getStatus,0);
        queryWrapper.eq(SysUser::getDelFlag,0);
        queryWrapper.like(SysUser::getUserName,req.getName());
        List<SysUser> users = sysUserMapper.selectList(queryWrapper);
        List<FindSysUsersByNameVO> list = users.stream().map(v -> {
            FindSysUsersByNameVO vo = new FindSysUsersByNameVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<Long> findUserIdByApiKey(String apiKey) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        SysUser sysUser = new SysUser();
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");
        sysUser.setApiKey(apiKey);
        queryWrapper.setEntity(sysUser);
        List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
        if (sysUserList != null && sysUserList.size() > 0) {
            return Result.success(sysUserList.get(0).getUserId());
        }
        return Result.OK;
    }

    /**
     * 根据条件分页查询用户列表
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public Map<String,Object> selectUserList(SelectUserListInput user)
    {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(user.getUserName())){
            queryWrapper.like(SysUser::getUserName,user.getUserName());
        }
        if(StringUtils.isNotEmpty(user.getPhonenumber())){
            queryWrapper.like(SysUser::getPhonenumber,user.getPhonenumber());
        }
        if(StringUtils.isNotEmpty(user.getStatus())){
            queryWrapper.eq(SysUser::getStatus,user.getStatus());
        }
        if(user.getParams() != null){
            String beginTime = (String) user.getParams().get("beginTime");
            String endTime = (String) user.getParams().get("endTime");
            if(StringUtils.isNotEmpty(beginTime)){
                queryWrapper.ge(SysUser::getCreateTime,beginTime);
            }
            if(StringUtils.isNotEmpty(endTime)){
                queryWrapper.le(SysUser::getCreateTime,endTime);
            }
        }
        /**
         * 查询用户信息
         */
        queryWrapper.eq(SysUser::getDelFlag,0);
        log.info("查询用户,入参:{}",queryWrapper.toString());
        IPage<SysUser> page = sysUserMapper.selectPage(new Page<>(user.getPageNum(), user.getPageSize()), queryWrapper);
        log.info("查询用户,出参:{}",page.toString());
        List<SysUser> list = page.getRecords().stream().filter(v -> v.getUserId() != null && 1L != v.getUserId()).collect(Collectors.toList());
        /**
         * 获取用户的总算力和总收益
         */
        if(list.size() > 0){
            List<Long> userIds = list.stream().map(v -> v.getUserId()).collect(Collectors.toList());
            UserMinerBO userMinerBO = new UserMinerBO();
            userMinerBO.setUserIds(userIds);
            userMinerBO.setAsc(user.isAsc());
            userMinerBO.setCloumName(user.getCloumName());
            log.info("查询总算力和总收益,入参：{}",JSON.toJSONString(userMinerBO));
            Result<List<AggMinerVO>> aggMinerResult = aggMinerFeignClient.findBatchMinerByUserId(userMinerBO);
            log.info("查询总算力和总收益,出参：{}",JSON.toJSONString(aggMinerResult));
            if(ErrorCode.MYB_000000.getCode().equals(aggMinerResult.getCode())){
                List<AggMinerVO> aggMiners = aggMinerResult.getData();
                Map<Long,AggMinerVO> maps = new HashMap<>();
                aggMiners.stream().forEach(v->maps.put(v.getUserId(),v));

                // 根据userIdList查询所有userId和费率的map
                Long currencyId = HttpRequestUtil.getCurrencyId();
                String type = CurrencyEnum.getCurrency(currencyId).name();
                log.info("根据userIdList查询userId和费率的map入参userIds：【{}】,type:【{}】",userIds,type);
                Result<Map<Long,BigDecimal>> result = currencyRateFeign.getUserIdRateMapByUserIdList(userIds,type);
                log.info("根据userIdList查询userId和费率的map出参",JSON.toJSON(result));
                if(!ErrorCode.MYB_000000.getCode().equals(result.getCode())){
                    throw MyException.fail(UserError.MYB_333333.getCode(),"根据userIdList查询userId和费率的map失败");
                }

                //将总算力和总收益加入到 SysUser 对象中
                list.stream().forEach(v->{
                    AggMinerVO vo = maps.get(v.getUserId());
                    v.setPowerAvailable(vo != null ? vo.getPowerAvailable() : new BigDecimal(0));
                    v.setTotalBlockAward(vo != null ? BigDecimalUtil.formatFour(vo.getTotalBlockAward()) : new BigDecimal(0));
                    v.setFeeRate(vo != null ? vo.getFeeRate() : result.getData().get(v.getUserId()));
                    v.setPassword(null);
                });
            }
        }
        //组装返回值
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("total",page.getTotal());
        map.put("rows",list);
        return map;
    }

    /**
     * 根据条件分页查询用户列表-起亚币
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public Map<String,Object> selectChiaUserList(SelectUserListInput user)
    {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(user.getUserName())){
            queryWrapper.like(SysUser::getUserName,user.getUserName());
        }
        if(StringUtils.isNotEmpty(user.getPhonenumber())){
            queryWrapper.like(SysUser::getPhonenumber,user.getPhonenumber());
        }
        if(StringUtils.isNotEmpty(user.getStatus())){
            queryWrapper.eq(SysUser::getStatus,user.getStatus());
        }
        if(user.getParams() != null){
            String beginTime = (String) user.getParams().get("beginTime");
            String endTime = (String) user.getParams().get("endTime");
            if(StringUtils.isNotEmpty(beginTime)){
                queryWrapper.ge(SysUser::getCreateTime,beginTime);
            }
            if(StringUtils.isNotEmpty(endTime)){
                queryWrapper.le(SysUser::getCreateTime,endTime);
            }
        }
        /**
         * 查询用户信息
         */
        queryWrapper.eq(SysUser::getDelFlag,0);
        log.info("查询用户,入参:{}",queryWrapper.toString());
        IPage<SysUser> page = sysUserMapper.selectPage(new Page<>(user.getPageNum(), user.getPageSize()), queryWrapper);
        log.info("查询用户,出参:{}",page.toString());
        List<SysUser> list = page.getRecords().stream().filter(v -> v.getUserId() != null && 1L != v.getUserId()).collect(Collectors.toList());
        /**
         * 获取用户的总算力和总收益
         */
        if(list.size() > 0){
            List<Long> userIds = list.stream().map(v -> v.getUserId()).collect(Collectors.toList());
            UserMinerBO userMinerBO = new UserMinerBO();
            userMinerBO.setUserIds(userIds);
            userMinerBO.setAsc(user.isAsc());
            userMinerBO.setCloumName(user.getCloumName());
            log.info("查询总算力和总收益,入参：{}",JSON.toJSONString(userMinerBO));
            Result<List<AggMinerVO>> aggMinerResult = aggChiaMinerFeign.findBatchChiaMinerByUserId(userMinerBO);
            log.info("查询总算力和总收益,出参：{}",JSON.toJSONString(aggMinerResult));
            if(ErrorCode.MYB_000000.getCode().equals(aggMinerResult.getCode())){
                List<AggMinerVO> aggMiners = aggMinerResult.getData();
                Map<Long,AggMinerVO> maps = new HashMap<>();
                aggMiners.stream().forEach(v->maps.put(v.getUserId(),v));

                // 根据userIdList查询所有userId和费率的map
                Long currencyId = HttpRequestUtil.getCurrencyId();
                String type = CurrencyEnum.getCurrency(currencyId).name();
                log.info("根据userIdList查询userId和费率的map入参userIds：【{}】,type:【{}】",userIds,type);
                Result<Map<Long,BigDecimal>> result = currencyRateFeign.getUserIdRateMapByUserIdList(userIds,type);
                log.info("根据userIdList查询userId和费率的map出参",JSON.toJSON(result));
                if(!ErrorCode.MYB_000000.getCode().equals(result.getCode())){
                    throw MyException.fail(UserError.MYB_333333.getCode(),"根据userIdList查询userId和费率的map失败");
                }

                //将总算力和总收益加入到 SysUser 对象中
                list.stream().forEach(v->{
                    AggMinerVO vo = maps.get(v.getUserId());
                    v.setPowerAvailable(vo != null ? vo.getPowerAvailable() : new BigDecimal(0));
                    v.setTotalBlockAward(vo != null ? BigDecimalUtil.formatFour(vo.getTotalBlockAward()) : new BigDecimal(0));
                    v.setFeeRate(vo != null ? vo.getFeeRate() : result.getData().get(v.getUserId()));
                    v.setPassword(null);
                });
            }
        }
        //组装返回值
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("total",page.getTotal());
        map.put("rows",list);
        return map;
    }

    /**
     * 通过用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId){
        SysUser user = sysUserMapper.selectById(userId);

        List<FindUserRateVO> list = feeRateManager.findUserRate(userId);
        List<CurrencyRateBO> lt = list.stream().map(v -> {
            CurrencyRateBO currencyRateBO = new CurrencyRateBO();
            currencyRateBO.setFeeRate(v.getFeeRate().doubleValue());
            currencyRateBO.setType(v.getType());
            return currencyRateBO;
        }).collect(Collectors.toList());
        user.setRats(lt);
        return user;
    }

    /**
     * 校验用户名称是否唯一
     * @param userName 用户名称
     * @return 结果
     */
    public String checkUserNameUnique(String userName)
    {
        int count = sysUserMapper.checkUserNameUnique(userName);
        if (count > 0)
        {
            return 1+"";
        }
        return "SysPermissionService";
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public String checkPhoneUnique(SysUser user)
    {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = sysUserMapper.checkPhoneUnique(user.getPhonenumber());
        if (info != null && info.getUserId().longValue() != userId.longValue())
        {
            return "1";
        }
        return "SysPermissionService";
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public String checkEmailUnique(SysUser user)
    {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = sysUserMapper.checkEmailUnique(user.getEmail());
        if (info!=null && info.getUserId().longValue() != userId.longValue()){
            return "1";
        }
        return "SysPermissionService";
    }

    @Transactional
    public int insertUser(SysUser user)
    {
        // 新增用户信息
        user.setCreateTime(LocalDateTime.now());
        int rows = sysUserMapper.insert(user);
        // 新增用户与角色管理
        insertUserRole(user);
        /**
         * 保存费率信息
         */
        feeRateManager.saveOrUpdateFeeRate(user.getUserId(),user.getRats());
        return rows;
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        Long roles = user.getRoleIds();
        if (roles!=null)
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            SysUserRole ur = new SysUserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roles);
            list.add(ur);
            if (list.size() > 0)
            {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        /**
         * 修改币种费率
         */
        feeRateManager.saveOrUpdateFeeRate(user.getUserId(),user.getRats());
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        return sysUserMapper.updateUser(user);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Transactional
    public int deleteUserByIds(Long[] userIds){
        for (Long userId : userIds){
            SysUser sysUser = new SysUser();
            sysUser.setUserId(userId);
            checkUserAllowed(sysUser);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        return sysUserMapper.deleteUserByIds(userIds);
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (user.getUserId()!=null && user.isAdmin()){
            throw MyException.fail(UserError.MYB_333333.getCode(),"不允许操作超级管理员用户");
        }
    }

    /**
     * 重置用户密码
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user)
    {
        return sysUserMapper.updateUser(user);
    }

    /**
     * 修改用户状态
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user)
    {
        return sysUserMapper.updateUser(user);
    }
    /**
     * 修改用户头像
     * @param userId 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public boolean updateUserAvatar(Long userId, String avatar)
    {
        return sysUserMapper.updateUserAvatar(userId, avatar) > 0;
    }

    /**
     * 冒充用户登录
     * @param userId
     * @return
     */
    public Map<String,Object> Impersonation(Long userId){
        /**
         * 校验userId
         */
        if(userId == 0){
            throw MyException.fail(UserError.MYB_333333.getCode(),"userId 为空");
        }
        SysUser sysUser = sysUserMapper.selectById(userId);
        if(sysUser == null){
            throw MyException.fail(UserError.MYB_333333.getCode(),"userId 错误");
        }
        /**
         * 生成token
         */
        //默认当前币种时fil币
        Long currencyId = Constants.fileCurrencyId;
        //生成token
        String token = JwtUtil.createToken(sysUser.getUserId(),currencyId,Constants.WEB);
        /**
         * 组装响应数据
         */
        Map<String,Object> result = new HashMap<>();
        result.put("code",ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        result.put(SystemConstants.TOKEN,token);
        redisUtils.set(token,currencyId+"",ruoYiConfig.getJwtMinutes(),TimeUnit.MINUTES);
        return result;
    }

    public Result<ChangeCurrencyVO> changeCurrency(ChangeCurrencyBO changeCurrencyBO){
        Long currencyId = changeCurrencyBO.getCurrencyId();
        //生成token
        String token = JwtUtil.createToken(HttpRequestUtil.getUserId(),currencyId,Constants.WEB);
        redisUtils.set(token,currencyId+"",ruoYiConfig.getJwtMinutes(),TimeUnit.MINUTES);
        ChangeCurrencyVO changeCurrencyVO = new ChangeCurrencyVO();
        changeCurrencyVO.setToken(token);
        return Result.success(changeCurrencyVO);
    }

    /**
     * 修改用户基本信息
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(SysUser user){
        return sysUserMapper.updateUser(user);
    }

    /**
     * 修改用户信息
     * @param sysUserBO
     * @return
     */
    @Override
    public Map<String,Object> updateProfile(SysUserBO sysUserBO){
        SysUser user = new SysUser();
        BeanUtils.copyProperties(sysUserBO,user);
        if(user.getUserId() == null || user.getUserId() ==0){
            throw MyException.fail(UserError.MYB_333333.getCode(),"userId为空");
        }

        Long loginUserId = HttpRequestUtil.getUserId();
        //检查验证码是否正确
        String smsCode = String.format(SystemConstants.SMSKEY,sysUserBO.getServiceName(),loginUserId);
        String code = redisUtils.get(smsCode);
        if(StringUtils.isEmpty(code)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"验证码错误");
        }
        if(!code.equals(sysUserBO.getSmsCode())){
            throw MyException.fail(UserError.MYB_333333.getCode(),"验证码错误");
        }

        Long userId = user.getUserId();
        if (loginUserId.longValue() != userId.longValue()) {
            throw MyException.fail(UserError.MYB_333333.getCode(),"只能修改登录人自己的信息");
        }

        log.info("查询用户信息，userId = {}",userId);
        SysUser sysUser = sysUserMapper.selectById(userId);
        log.info("查询用户信息,结果:{}", JSON.toJSONString(sysUser));
        if(sysUser == null){
            throw MyException.fail(UserError.MYB_333333.getCode(),"userId错误");
        }
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getUserId,userId);
        updateWrapper.set(SysUser::getNickName,user.getNickName());
        updateWrapper.set(SysUser::getPhonenumber,user.getPhonenumber());
        updateWrapper.set(SysUser::getEmail,user.getEmail());
//        updateWrapper.set(SysUser::getSex,user.getSex());
        sysUserMapper.update(null,updateWrapper);

        // 清空验证码
        log.info("清空验证码smsCode:【{}】,code:【{}】",smsCode,code);
        redisUtils.delete(smsCode);
        String smsCodeTime = String.format(SystemConstants.SMSKEYTIME,sysUserBO.getServiceName(),loginUserId);
        redisUtils.delete(smsCodeTime);

        Map<String,Object> result = new HashMap<>();
        result.put("code",ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        return result;
    }

    public Result updatePwd(String oldPassword, String newPassword){
        SysUser loginUser = sysUserMapper.selectById(HttpRequestUtil.getUserId());
        if(!loginUser.getPassword().equals(AESUtil.encrypt(oldPassword))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"旧密码错误");
        }
        if(loginUser.getPassword().equals(AESUtil.encrypt(newPassword))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"新密码不能与旧密码相同");
        }
        int oldLength = oldPassword.length();
        if(CommonUtil.isContainChinese(oldPassword) || oldLength < 8 || oldLength > 32){
            throw MyException.fail(UserError.MYB_333333.getCode(),"旧密码格式错误");
        }
        int newLength = newPassword.length();
        if(CommonUtil.isContainChinese(newPassword) || newLength < 8 || newLength > 32){
            throw MyException.fail(UserError.MYB_333333.getCode(),"新密码格式错误");
        }
        LambdaUpdateWrapper<SysUser> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(SysUser::getUserId,loginUser.getUserId());
        lambdaUpdateWrapper.set(SysUser::getPassword,AESUtil.encrypt(newPassword));
        sysUserMapper.update(null,lambdaUpdateWrapper);
        return Result.OK;
    }

}
