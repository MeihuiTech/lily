package com.mei.hui.browser.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BlockService {

    Map<String, BigDecimal> blockRanking(int range, List<String> minerIds) throws IOException;
}
