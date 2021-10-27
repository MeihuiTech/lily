package com.mei.hui.browser.service;

import com.mei.hui.browser.model.PowerRankingVO;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.PageResult;

import java.io.IOException;

public interface PowerService {

    PageResult<PowerRankingVO> powerRanking(BasePage page) throws IOException;
}
