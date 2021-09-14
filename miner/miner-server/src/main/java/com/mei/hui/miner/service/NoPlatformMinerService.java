package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 非平台矿工,仅用于大屏显示 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
public interface NoPlatformMinerService extends IService<NoPlatformMiner> {
    Result findNoPlatformMiners();

    Result noPlatformMiner(NoPlatformMiner noPlatformMiner);

    Result saveOrUpdate(@RequestBody NoPlatformAddBO bo);

    Result delete(String minerId);

    PageResult<NoPlatformVOPage> pageList(@RequestBody NoPlatformBOPage bo);

    void setPlatformBaseInfo(PlatformBaseInfoVO vo);

    void setFindDiskSizeInfo(List<FindDiskSizeInfoBO> list);

    void setAvailablePower(List<AvailablePowerVO> list);

    void setAccountInfo(List<AccountInfoVO> list);
}
