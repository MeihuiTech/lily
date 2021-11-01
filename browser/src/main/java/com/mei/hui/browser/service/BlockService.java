package com.mei.hui.browser.service;

import com.mei.hui.browser.model.BlockPageListVO;
import com.mei.hui.browser.model.RankingBO;
import com.mei.hui.util.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BlockService {

    Map<String, BigDecimal> twentyFourBlockIncr(int range, List<String> minerIds) throws IOException;

    PageResult<BlockPageListVO> blockPageList(@RequestBody RankingBO rankingBO) throws IOException;
}
