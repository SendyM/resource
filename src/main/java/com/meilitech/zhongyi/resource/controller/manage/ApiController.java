package com.meilitech.zhongyi.resource.controller.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meilitech.zhongyi.resource.controller.ResourceController;
import com.meilitech.zhongyi.resource.dao.*;
import com.meilitech.zhongyi.resource.util.ToolsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/manage")
@EnableAutoConfiguration
@RestController
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    DomainSourceRepository domainSourceRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    Environment env;

    @GetMapping
    @RequestMapping("/import/resource")
    public String create(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {


        log.info(httpEntity.getBody());
        log.info(reqMap.getOrDefault("data", "").toString());


        String flag = reqMap.getOrDefault("flag", "").toString();
        if (!flag.isEmpty()) {
            int i = 0;
            try {
                //导入数据
                String dataPath = env.getProperty("domain.data.path") + "domain.txt";
                File conentFile = new File(dataPath);
                if (!conentFile.exists()) {
                    return "empty";
                }

                BufferedReader bufread;
                String read;
                bufread = new BufferedReader(new FileReader(conentFile));

                while ((read = bufread.readLine()) != null) {
                    i++;
                    read = read.trim();
                    if (read.isEmpty()) continue;

                    Instant nowInstant = Instant.now();
                    LocalDate ymd = nowInstant.atZone(ZoneId.systemDefault()).toLocalDate();
                    Date nowData = new Date();

                    ResourceDao resource = new ResourceDao();
                    resource.setResourceId(UUID.randomUUID());
                    resource.setDomain(read);
                    resource.setUrl("http://" + read);
                    resource.setStatus(DomainSourceDao.STATUS_OK);
                    resource.setProvider(ResourceDao.Provider.TASKCENTER.toString());
                    resource.setUrlType(ResourceDao.UrlType.DOMAIN.getUrlType());
                    resource.setCrawlerTime(nowData);
                    resource.setUpdateTime(nowData);
                    resource.setYmd(ymd);
                    resource.setCategoryIds(ResourceDao.CategoryIds.UNKNOWN.toString());
                    resource.setResourceTaskId(ToolsUtil.getTaskId());
                    resource.setCreateTime(nowInstant);
                    resource.setPublishTime(nowData);
                    resource.setTitle("");
                    resource.setRank((int)Math.random()*30);
                    resource.setLanguage("");
                    resource.setKeywords("");
                    resource.setDescription("");
                    resource.setCharset("");
                    resource.setCountry("");


                    try {
                        resourceRepository.save(resource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return "error";
            }

            return "ok" + Integer.toString(i);
        }

        return "end";

    }


}
