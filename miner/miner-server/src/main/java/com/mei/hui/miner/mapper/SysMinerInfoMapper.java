package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTotalEarning;
import com.mei.hui.miner.model.SysMinerInfoBO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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

    public IPage<SysMinerInfo> pageMinerInfo(IPage<SysMinerInfo> page, SysMinerInfoBO sysMinerInfoBO);
}
