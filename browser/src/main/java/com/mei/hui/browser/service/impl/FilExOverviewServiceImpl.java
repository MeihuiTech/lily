package com.mei.hui.browser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.browser.entity.FilExOverview;
import com.mei.hui.browser.mapper.FilExOverviewMapper;
import com.mei.hui.browser.service.FilExOverviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:56
 **/
@Service
@Slf4j
public class FilExOverviewServiceImpl extends ServiceImpl<FilExOverviewMapper,FilExOverview>  implements FilExOverviewService {

    @Autowired
    private FilExOverviewMapper filExOverviewMapper;

}
