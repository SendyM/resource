package com.meilitech.zhongyi.resource.task;

import com.meilitech.zhongyi.resource.service.UpdateRankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Sendy
 */
@Component
public class UpdateRankTasks {
    private static final Logger log = LoggerFactory.getLogger(UpdateRankTasks.class);

    @Autowired
    UpdateRankService updateRankService;
    /**
     * 每天凌晨1点执行一次 0 0 1 * * ?
     * 获取rank值并更新到resource定时任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void  updateRank() {
        log.info("----------------------更新Rank值定时任务开始---------------------");
        updateRankService.getAndUpdateRank();
        log.info("----------------------更新Rank值定时任务结束---------------------");
    }
}
