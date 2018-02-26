package com.meilitech.zhongyi.resource.controller.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meilitech.zhongyi.resource.controller.ResourceController;
import com.meilitech.zhongyi.resource.dao.ResourceDetailRepository;
import com.meilitech.zhongyi.resource.dao.DomainSourceDao;
import com.meilitech.zhongyi.resource.dao.DomainSourceRepository;
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
    ResourceDetailRepository domainRepository;

    @Autowired
    Environment env;

//    @GetMapping
//    @RequestMapping("/get")
//    public String create(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {
//
//
//        log.info(httpEntity.getBody());
//        log.info(reqMap.getOrDefault("data", "").toString());
//        JSONObject data = (JSONObject) JSON.parse(reqMap.getOrDefault("data", new JSONObject()).toString());
//
//        String flag = reqMap.getOrDefault("flag", "").toString();
//        if (flag.isEmpty()) {
//            Iterable<DomainSourceDao> res = domainSourceRepository.getData(new Double(100 * Math.random()).intValue());
//            ArrayList<String> domainList = new ArrayList<>();
//            for (DomainSourceDao d : res) {
//                System.out.println(d.getDomain());
//                domainList.add(d.getDomain());
//
//                try {
//                    domainSourceRepository.updateSomeStatus(d.getId(), d.getDomain(), DomainSourceDao.STATUS_DEL);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            String[] resStr = new String[domainList.size()];
//            int i = 0;
//            for (String v : domainList) {
//                resStr[i++] = v;
//            }
//            return String.join(",", resStr);
//        } else {
//            int i = 0;
//            try {
//                //导入数据
//                String dataPath = env.getProperty("domain.data.path") + "domain.txt";
//                File conentFile = new File(dataPath);
//                if (!conentFile.exists()) {
//                    return "empty";
//                }
//
//                BufferedReader bufread;
//                String read;
//                bufread = new BufferedReader(new FileReader(conentFile));
//
//                while ((read = bufread.readLine()) != null) {
//                    i++;
//                    read = read.trim();
//                    if (read.isEmpty()) continue;
//                    DomainSourceDao domainSourceDao = new DomainSourceDao();
//                    domainSourceDao.setId(UUID.randomUUID());
//                    domainSourceDao.setDomain(read);
//                    domainSourceDao.setStatus(DomainSourceDao.STATUS_OK);
//
//                    DomainDao domainDao = new DomainDao();
//                    domainDao.setDomain(read);
//                    domainDao.setYmd(new SimpleDateFormat("yyyyMMdd").format(new Date()));
//                    try {
//                        domainSourceRepository.save(domainSourceDao);
//                        domainRepository.save(domainDao);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                return "error";
//            }
//
//            return "ok" + Integer.toString(i);
//        }
//
//
//    }


}
