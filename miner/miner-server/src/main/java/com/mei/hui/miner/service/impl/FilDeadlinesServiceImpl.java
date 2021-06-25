package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilDeadlines;
import com.mei.hui.miner.mapper.FilDeadlinesMapper;
import com.mei.hui.miner.model.FilDeadlinesListVO;
import com.mei.hui.miner.model.FilDeadlinesNinetySixVO;
import com.mei.hui.miner.service.FilDeadlinesService;
import com.mei.hui.util.Result;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * filcoin 矿工窗口记录表 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Slf4j
@Service
public class FilDeadlinesServiceImpl extends ServiceImpl<FilDeadlinesMapper, FilDeadlines> implements FilDeadlinesService {

    @Autowired
    private FilDeadlinesMapper filDeadlinesMapper;

    /*用户首页WindowPoSt的96个窗口*/
    @Override
    public Result selectFilDeadlinesNinetySixList() {
        List<FilDeadlinesListVO> filDeadlinesList = filDeadlinesMapper.selectFilDeadlinesNinetySixList();
        log.info("用户首页WindowPoSt的96个窗口出参：【{}】",JSON.toJSON(filDeadlinesList));
        if (filDeadlinesList == null || filDeadlinesList.size() < 1) {
            return Result.OK;
        }
        FilDeadlinesNinetySixVO filDeadlinesNinetySixVO = new FilDeadlinesNinetySixVO();
        //今天矿工窗口记录
        List<FilDeadlinesListVO> todayFilDeadlinesList = new ArrayList<>();
        //昨天矿工窗口记录
        List<FilDeadlinesListVO> yesterdayFilDeadlinesList = new ArrayList<>();
        //当前窗口序号
        Integer deadline;
        for (int i = 0;i<filDeadlinesList.size();i++) {
            if (i<48){
                todayFilDeadlinesList.add(filDeadlinesList.get(i));
                if (1 == filDeadlinesList.get(i).getIsCurrent()) {
                    deadline = filDeadlinesList.get(i).getDeadline();
                    filDeadlinesNinetySixVO.setDeadline(deadline);
                }
            } else {
                yesterdayFilDeadlinesList.add(filDeadlinesList.get(i));
            }
        }
        filDeadlinesNinetySixVO.setTodayFilDeadlinesList(todayFilDeadlinesList);
        filDeadlinesNinetySixVO.setYesterdayFilDeadlinesList(yesterdayFilDeadlinesList);
        return Result.success(filDeadlinesNinetySixVO);
    }


}
