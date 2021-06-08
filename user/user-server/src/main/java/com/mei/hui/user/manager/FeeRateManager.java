package com.mei.hui.user.manager;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.feign.feignClient.CurrencyRateFeign;
import com.mei.hui.miner.feign.vo.CurrencyRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateVO;
import com.mei.hui.miner.feign.vo.SaveFeeRateBO;
import com.mei.hui.user.common.UserError;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class FeeRateManager {
    @Autowired
    private CurrencyRateFeign currencyRateFeign;

    /**
     * 查询用户的币种费率
     * @param userId
     * @return
     */
    public List<FindUserRateVO> findUserRate(Long userId,String type){
        FindUserRateBO findUserRateBO = new FindUserRateBO();
        findUserRateBO.setUserId(userId);
        if(StringUtils.isNotEmpty(type)){
            findUserRateBO.setType(type);
        }
        log.info("查询币种费率，入参:{}", JSON.toJSONString(findUserRateBO));
        Result<List<FindUserRateVO>> result = currencyRateFeign.findUserRate(findUserRateBO);
        log.info("查询币种费率，出参:{}", JSON.toJSONString(result));
        if(!ErrorCode.MYB_000000.getCode().equals(result.getCode())){
            throw MyException.fail(result.getCode(),result.getMsg());
        }
        return result.getData();
    }

    /**
     * 新增或更新用户币种费率
     * @param userId
     * @param rats
     */
    public void saveOrUpdateFeeRate(Long userId,List<CurrencyRateBO> rats){
        if(userId == null || userId <= 0){
            throw MyException.fail(UserError.MYB_333333.getCode(),"用户id错误");
        }
        if(rats == null || rats.size() == 0){
            throw MyException.fail(UserError.MYB_333333.getCode(),"费率不能为空");
        }
        /**
         * 保存费率信息
         */
        SaveFeeRateBO saveFeeRateBO = new SaveFeeRateBO();
        saveFeeRateBO.setUserId(userId);
        saveFeeRateBO.setRats(rats);
        Result result = currencyRateFeign.saveOrUpdateFeeRate(saveFeeRateBO);
        if(!ErrorCode.MYB_000000.getCode().equals(result.getCode())){
            throw MyException.fail(result.getCode(),result.getMsg());
        }
    }
}
