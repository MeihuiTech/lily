package com.mei.hui.browser.service;

import com.mei.hui.browser.model.PowerRankingVO;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.PageResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PowerService {

    PageResult<PowerRankingVO> powerRanking(BasePage page) throws IOException;

    Map<String, BigDecimal> twentyFourPowerIncr(int range, List<String> minerIds) throws IOException;

    Map<String,BigDecimal> findMinerPower(List<String> minerIds) throws IOException;
}
