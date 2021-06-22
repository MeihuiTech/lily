package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTotalEarning;
import com.mei.hui.miner.feign.vo.FilUserMoneyVO;
import com.mei.hui.miner.model.PowerAvailableFilVO;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.SysMinerInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 矿工信息Mapper接口
 *
 * @author ruoyi
 * @date 2021-03-02
 */
@Repository
public interface SysMinerInfoMapper extends BaseMapper<SysMinerInfo> {
    /**
     * 查询矿工信息
     *
     * @param id 矿工信息ID
     * @return 矿工信息
     */
    public SysMinerInfo selectSysMinerInfoById(Long id);

    /**
     * 查询矿工信息列表
     *
     * @param sysMinerInfo 矿工信息
     * @return 矿工信息集合
     */
    public List<SysMinerInfo> selectSysMinerInfoList(SysMinerInfo sysMinerInfo);

    /**
     * 新增矿工信息
     *
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    public int insertSysMinerInfo(SysMinerInfo sysMinerInfo);

    /**
     * 修改矿工信息
     *
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    public int updateSysMinerInfo(SysMinerInfo sysMinerInfo);

    /**
     * 删除矿工信息
     *
     * @param id 矿工信息ID
     * @return 结果
     */
    public int deleteSysMinerInfoById(Long id);

    /**
     * 批量删除矿工信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysMinerInfoByIds(Long[] ids);

    SysMinerInfo selectSysMinerInfoByMinerId(String minerId);

    SysMinerInfo selectSysMinerInfoByUserIdAndMinerId(@Param("userId") Long userId, @Param("minerId") String minerId);

    SysTotalEarning selectTotalEarningAndAwardByUserId(@Param("minerId") String minerId);

    Long countByMinerId(@Param("minerId") String minerId);

    public IPage<SysMinerInfoVO> pageMinerInfo(IPage<SysMinerInfo> page, @Param("userId") Long userId, @Param("isAsc") boolean isAsc, @Param("cloumName")String cloumName);

    /**
    * 查询FIL币矿工信息表里所有的当天出块份数
    * 
    * @description 
    * @author shangbin
    * @date 2021/5/28 15:58
    * @param [] 
    * @return java.math.BigDecimal 
    * @version v1.0.0
    */
    public Long selectAllBlocksPerDay();

    /**
    * 管理员首页-旷工统计数据-平台总资产
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 17:05
    * @param []
    * @return java.math.BigDecimal
    * @version v1.0.0
    */
    public BigDecimal selectAllBalanceMinerAccount();

    /**
    * 管理员首页-旷工统计数据-平台有效算力
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 17:16
    * @param []
    * @return java.math.BigDecimal
    * @version v1.0.0
    */
    public BigDecimal selectAllPowerAvailable();

    /**
    * 管理员首页-旷工统计数据-活跃旷工
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 17:22
    * @param []
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectAllMinerIdCount();

    /**
    * 管理员首页-平台有效算力排行榜
    *
    * @description
    * @author shangbin
    * @date 2021/5/29 14:19
    * @param [yesterDayDate, allPowerAvailable]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.model.PowerAvailableFilVO>
    * @version v1.0.0
    */
    public IPage<PowerAvailableFilVO> powerAvailablePage(Page<PowerAvailableFilVO> powerAvailableFilVOPage, @Param("yesterDayDate") String yesterDayDate, @Param("allPowerAvailable") BigDecimal allPowerAvailable);

    /**
    * 分页查询用户收益列表
    *
    * @description
    * @author shangbin
    * @date 2021/6/22 16:40
    * @param [page, userId, cloumName, asc, userIdList]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.feign.vo.FilUserMoneyVO>
    * @version v1.4.0
    */
    public IPage<FilUserMoneyVO> selectUserMoneyList(Page<com.mei.hui.miner.feign.vo.FilUserMoneyVO> page,@Param("userId") Long userId,
                                                     @Param("cloumName") String cloumName,@Param("isAsc") boolean isAsc,@Param("userIdList") List<Long> userIdList);
}
