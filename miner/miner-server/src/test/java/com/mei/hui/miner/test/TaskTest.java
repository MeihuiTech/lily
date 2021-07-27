package com.mei.hui.miner.test;

import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.common.task.UpdateNodeAddressTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication.class)
@Slf4j
public class TaskTest {


    @Test
    public void initSectorToRedis(){
        HttpUtil.doPost("http://10.10.15.2:8082/fil/reported/initSectorToRedis","");
    }


    @Test
    public void initSectorDuration(){
        HttpUtil.doPost("http://10.10.15.2:8082/fil/reported/initSectorDuration","");
    }

    @Test
    public void test(){
        DateTimeFormatter fmt = new DateTimeFormatterBuilder().appendPattern("yyyy-MM")
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        LocalDate date = LocalDate.parse("2021-07", fmt);
        log.info(date.toString());
    }


}
