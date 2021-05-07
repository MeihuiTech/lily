package com.mei.hui.miner.common.task;

import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.service.ISysMachineInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Component("machineTask")
public class MachineTask {
    private static final Logger log = LoggerFactory.getLogger(MachineTask.class);

    @Autowired
    private ISysMachineInfoService sysMachineInfoService;

    /**
     * 检测矿机是否离线
     * @param rowCount 每次从数据库获取多少条数据
     * @param duration 单位秒, 大于这个时间则认为已离线
     */
    public void updateMachineOnlineStatus(Integer rowCount, Integer duration) {

        log.info("updateMachineOnlineStatus start...");

        Date now = new Date();
        int offset = 0;
        while (true) {
            List<SysMachineInfo> sysMachineInfoList = sysMachineInfoService.selectSysMachineInfoByLimit(offset, rowCount);

            for (SysMachineInfo sysMachineInfo : sysMachineInfoList) {
                long date = sysMachineInfo.getUpdateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
                if ((now.getTime() - date) > (duration * 1000)) {
                    sysMachineInfo.setOnline(0);
                    int row = sysMachineInfoService.updateSysMachineInfo(sysMachineInfo);
                    System.out.println(row);
                }
            }

            if (sysMachineInfoList.size() < rowCount) {
                break;
            }

            offset += rowCount;
        }

        log.info("updateMachineOnlineStatus end...");
    }
}
