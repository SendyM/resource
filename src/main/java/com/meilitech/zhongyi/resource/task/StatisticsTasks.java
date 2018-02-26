package com.meilitech.zhongyi.resource.task;

import com.meilitech.zhongyi.resource.dao.ResourceDao;
import com.meilitech.zhongyi.resource.dao.ResourceDetailRepository;
import com.meilitech.zhongyi.resource.dao.ResourceRepository;
import com.meilitech.zhongyi.resource.dao.ResourceStatisticsRepository;
import com.meilitech.zhongyi.resource.service.ResourceService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class StatisticsTasks {
    private static final Logger log = LoggerFactory.getLogger(StatisticsTasks.class);

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ResourceService resourceService;

    @Autowired
    Environment env;

    @Autowired
    ResourceStatisticsRepository resourceStatisticsRepository;


    @Scheduled(fixedDelay = 60000)
    public void run() {
        try {
            for(ResourceDao.Provider provider:ResourceDao.Provider.values()) {
                LocalDate ymd = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
                int dayUpdateCount = resourceRepository.countByYmdAndProvider(ymd,provider.toString());
                resourceStatisticsRepository.updateCounter(provider.toString(), ymd, dayUpdateCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
