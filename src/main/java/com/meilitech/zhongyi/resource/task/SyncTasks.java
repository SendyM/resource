package com.meilitech.zhongyi.resource.task;

import com.meilitech.zhongyi.resource.dao.*;
import com.meilitech.zhongyi.resource.service.ResourceService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SyncTasks {
    private static final Logger log = LoggerFactory.getLogger(SyncTasks.class);

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ResourceService resourceService;

    @Autowired
    Environment env;

    @Autowired
    ResourceDetailRepository domainRepository;


    //@Scheduled(fixedDelay = 60000)
    public void parse() {
//        String url = env.getProperty("resource.sync.url");
//        if (url == null) return;
//        try {
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpGet httpGet = new HttpGet(url);
//            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//
//            System.out.println("GET Response Status:: "
//                    + httpResponse.getStatusLine().getStatusCode());
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    httpResponse.getEntity().getContent()));
//
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = reader.readLine()) != null) {
//                response.append(inputLine);
//            }
//            reader.close();
//
//            // print result
//            System.out.println(response.toString());
//            String content = response.toString();
//
//
//            try {
//                if (content != null) {
//                    System.out.println("--------------------------------------");
//                    String[] domains = response.toString().split(",");
//                    for (int i = 0; i < domains.length; i++) {
//                        if(domains[i] == null || domains[i].isEmpty())continue;
//                        DomainDao domainDao = new DomainDao();
//                        domainDao.setDomain(domains[i]);
//                        domainDao.setYmd(new SimpleDateFormat("yyyyMMdd").format(new Date()));
//                        domainRepository.save(domainDao);
//                    }
//                    System.out.println("--------------------------------------");
//                }
//            } finally {
//                httpClient.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
