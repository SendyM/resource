package com.meilitech.zhongyi.resource.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.ReadFailureException;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import com.meilitech.zhongyi.resource.constants.SysError;
import com.meilitech.zhongyi.resource.dao.ResourceDao;
import com.meilitech.zhongyi.resource.dao.ResourceRepository;
import com.meilitech.zhongyi.resource.dao.UrlDao;
import com.meilitech.zhongyi.resource.dao.UrlRepository;
import com.meilitech.zhongyi.resource.service.ResourceService;
import com.meilitech.zhongyi.resource.util.FileUtil;
import com.meilitech.zhongyi.resource.util.RedissonUtil;
import com.meilitech.zhongyi.resource.util.ResponseTemplate;
import com.meilitech.zhongyi.resource.util.ToolsUtil;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.cassandra.convert.CassandraConverter;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@RequestMapping("/resource")
@RestController
@EnableAutoConfiguration
public class ResourceController {
    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    CassandraOperations cassandraTemplate;
    @Autowired
    UrlRepository urlRepository;
    @Autowired
    ResourceService resourceService;
    @Autowired
    Environment env;
    private Object list;


    @PostMapping
    @RequestMapping("/create")
    public ResponseTemplate create(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {


        int err = 0;
        String errMsg = "ok";
        ResponseTemplate res = new ResponseTemplate();


        log.info(httpEntity.getBody());
        log.info(reqMap.getOrDefault("data", "").toString());
        JSONObject data = (JSONObject) JSON.parse(reqMap.getOrDefault("data", new JSONObject()).toString());


        JSONObject body = (JSONObject) data.getOrDefault("body", new JSONObject());
        JSONObject request = (JSONObject) body.getOrDefault("request", new JSONObject());


        String type = (String) request.get("type");
        JSONObject contentParams = (JSONObject) request.get("content");
        String contentColName = (String) request.get("contentColName");
        String provider = (String) request.get("provider");
        if (provider == null || provider.isEmpty()) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }
        if (provider.equals(ResourceDao.Provider.TASKCENTER)) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }

        //  JSONObject contentParams;
//        if (content.isEmpty()) {
//            res.setResultCode(100102);
//            res.setResultMsg("上传参数content不能为空");
//            return res;
//        } else {
//            contentParams = (JSONObject) JSON.parse(content);
//        }


        if (type.equals("2")) {
            //线上环境
            //String dataPath = "/home/resource/" + contentParams.get("path");
            //笔记本
            //String dataPath = "/Users/glone/work/zhongyi/resource.meilitech.com/data/" + contentParams.get("path");
            //台式
            //String dataPath = "/Users/huxiaoer/work/zhongyi/resource.meilitech.com/data/" + contentParams.get("path");
            String dataPath = env.getProperty("resource.data.path") + contentParams.get("path");
            res = resourceService.importFromFile(dataPath, provider);
        }


        return res;
    }

    @PostMapping
    @RequestMapping("/updateMaxCrawlCount")
    //更新域名抓取url数量阈值
    public ResponseTemplate update(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {
        ResponseTemplate res = new ResponseTemplate();
        JSONObject data = (JSONObject) JSON.parse(reqMap.getOrDefault("data", new JSONObject()).toString());
        JSONObject body = (JSONObject) data.getOrDefault("body", new JSONObject());
        JSONObject request = (JSONObject) body.getOrDefault("request", new JSONObject());

        String domain = (String) request.get("domain");
        int maxCrawlCount = Integer.valueOf((String) request.getOrDefault("maxCrawlCount", "20000"));

        String provider = (String) request.get("provider");
        if (provider == null || provider.isEmpty()) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }
        if (provider.equals(ResourceDao.Provider.TASKCENTER)) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }


        Select select = QueryBuilder.select().all().from("resource");
        select.where(QueryBuilder.eq("domain", domain));
        select.allowFiltering();
        select.enableTracing();

        ResultSet resultSet = null;
        CassandraConverter converter = cassandraTemplate.getConverter();
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
        } catch (ReadFailureException e) {
            e.printStackTrace();
            res.setResultCode("db_err");
            return res;
        }
        List<ResourceDao> rList = new ArrayList<>();
        if (resultSet != null) {
            int remaining = resultSet.getAvailableWithoutFetching();
            for (Row row : resultSet) {
                //Convert rows to chat objects
                ResourceDao chat = converter.read(ResourceDao.class, row);

                rList.add(chat);

                //If we can't move to the next row without fetching we break
                if (--remaining == 0) {
                    break;
                }
            }
        }


        Update.Where update = QueryBuilder.update("resource")
                .with(QueryBuilder.set("domain", domain))
                .where(QueryBuilder.eq("domain", domain));


        cassandraTemplate.getSession().execute(update);

        return res;
    }

//    @RequestMapping("/info/search")
//    public ResponseTemplate search(@RequestParam Map<String, Object> reqMap) {
//        List<ResourceDao> rdList = resourceRepository.findAll();
//
//
//
//        ResponseTemplate res = new ResponseTemplate();
//        res.setResponse("data", rdList);
//
//        return res;
//    }

    @RequestMapping("/info/search")
    public ResponseTemplate search(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {


        ResponseTemplate res = new ResponseTemplate();
        log.info(httpEntity.getBody());
        log.info(reqMap.getOrDefault("data", new JSONObject()).toString());
        JSONObject data = (JSONObject) JSON.parse(reqMap.getOrDefault("data", new JSONObject()).toString());


        JSONObject body = (JSONObject) data.getOrDefault("body", new JSONObject());
        JSONObject request = (JSONObject) body.getOrDefault("request", new JSONObject());
        JSONObject result = (JSONObject) body.getOrDefault("result", new JSONObject());

        String page = (String) request.getOrDefault("resultSign", "");
        String resourceTaskId = (String) request.getOrDefault("resourceTaskId", "");
        String domain = (String) request.getOrDefault("domain", "");
        String isLike = (String) request.getOrDefault("isLike", "0");
        int pageNum = Integer.valueOf((String) request.getOrDefault("pageNum", "20"));

        Select select = QueryBuilder.select().all().from("resource");

        do {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Timestamp minDate = null;
            Timestamp maxDate = null;
            try {
                minDate = new java.sql.Timestamp(new Date(0).getTime());
                Date parsedDate = dateFormat.parse("2019-01-01 00:00:00.000");
                maxDate = new java.sql.Timestamp(parsedDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                res.setResultCode("lastUpdateTimeErr");
                return res;
            }


            //如有分页
            if (page != null && !page.isEmpty()) {
                PagingState pagingState = PagingState.fromString(page);
                select.setPagingState(pagingState);
                break;
            }

            if (!resourceTaskId.isEmpty()) {
                select.where(QueryBuilder.eq("resourceTaskId", resourceTaskId));
                break;
            }

            if (!domain.isEmpty()) {
                if(isLike.equals("1")) {
                    select.where(QueryBuilder.like("domain", domain + "%"));
                }else{
                    select.where(QueryBuilder.eq("domain", domain));
                }
            }else{
                int rankMin = 0;
                int rankMax = 0;

                rankMin = Integer.valueOf((String) request.getOrDefault("rankMin", "0"));
                rankMax = Integer.valueOf((String) request.getOrDefault("rankMax", "0"));

                ArrayList rankList = new ArrayList();
                for (int i = rankMin; i <= rankMax; i++) rankList.add(i);
                select.where(QueryBuilder.in("rank", rankList));
            }


            String crawlerTimeStartParams = (String) request.getOrDefault("crawlerTimeStart", "");
            String crawlerTimeEndParams = (String) request.getOrDefault("crawlerTimeEnd", "");

            if(!crawlerTimeStartParams.isEmpty() && !crawlerTimeEndParams.isEmpty()) {
                try {
                    select.where(QueryBuilder.gte("crawlerTime", dateFormat.parse(crawlerTimeStartParams).getTime()));
                    select.where(QueryBuilder.lte("crawlerTime", dateFormat.parse(crawlerTimeEndParams).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    res.setResultCode("crawlerTimeErr");
                    return res;
                }
            }


            String publishTimeStartParams = (String) request.getOrDefault("publishTimeStart", "");
            String publishTimeEndParams = (String) request.getOrDefault("publishTimeEnd", "");

            if(!publishTimeStartParams.isEmpty() && !publishTimeEndParams.isEmpty()) {
                try {
                    select.where(QueryBuilder.gte("publishTime", dateFormat.parse(publishTimeStartParams).getTime()));
                    select.where(QueryBuilder.lte("publishTime", dateFormat.parse(publishTimeEndParams).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    res.setResultCode("publishTimeErr");
                    return res;
                }
            }


            String lastUpdateTimeStartParams = (String) request.getOrDefault("updateTimeStart", "");
            String lastUpdateTimeEndParams = (String) request.getOrDefault("updateTimeEnd", "");

            if(!lastUpdateTimeStartParams.isEmpty() && !lastUpdateTimeEndParams.isEmpty()) {
                long lastUpdateTimeStart;
                long lastUpdateTimeEnd;
                try {
                    lastUpdateTimeStart =  dateFormat.parse(lastUpdateTimeStartParams).getTime();
                    lastUpdateTimeEnd = dateFormat.parse(lastUpdateTimeEndParams).getTime();

                    select.where(QueryBuilder.gte("updatetime", lastUpdateTimeStart));
                    select.where(QueryBuilder.lte("updatetime", lastUpdateTimeEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                    res.setResultCode("updateTimeErr");
                    return res;
                }
            }

            String createTimeStartParams = (String) request.getOrDefault("createTimeStart", "");
            String createTimeEndParams = (String) request.getOrDefault("createTimeEnd", "");
            if(!createTimeStartParams.isEmpty() && !createTimeEndParams.isEmpty()) {
                long createTimeStart;
                long createTimeEnd;
                try {
                    createTimeStart = dateFormat.parse(createTimeStartParams).getTime();
                    createTimeEnd = dateFormat.parse(createTimeEndParams).getTime();

                    select.where(QueryBuilder.gte("createtime", createTimeStart));
                    select.where(QueryBuilder.lte("createtime", createTimeEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                    res.setResultCode("createtimeTimeErr");
                    return res;
                }
            }
        } while (false);

        select.setFetchSize(pageNum);
        select.allowFiltering();
        select.enableTracing();


        log.info(select.toString());


        //Used to map rows to Chat domain objects
        CassandraConverter converter = cassandraTemplate.getConverter();

        //Execute the query
        ResultSet resultSet = null;
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
        } catch (ReadFailureException e) {
            e.printStackTrace();
            res.setResultCode("db_err");
            return res;
        }

        if (resultSet != null) {
            //log.debug(resultSet.getExecutionInfo().getStatement().toString());

            //Get the next paging state
            PagingState newPagingState = resultSet.getExecutionInfo().getPagingState();
            //The number of rows that can be read without fetching
            int remaining = resultSet.getAvailableWithoutFetching();

            List<ResourceDao> rList = new ArrayList<>(pageNum);
            ArrayList domainList = new ArrayList<>();
            for (Row row : resultSet) {
                //Convert rows to chat objects
                ResourceDao chat = converter.read(ResourceDao.class, row);

                domainList.add(chat.getDomain().toString());
                rList.add(chat);


                //If we can't move to the next row without fetching we break
                if (--remaining == 0) {
                    break;
                }
            }

            //Serialise the next paging state
            String serializedNewPagingState = newPagingState != null ?
                    newPagingState.toString() :
                    null;


            res.setResponse("data", rList);
            if (serializedNewPagingState != null) {
                res.setResponse("resultSign", serializedNewPagingState);
            }

            //dayUpdateCount start

            Select select_url_statistics = QueryBuilder.select().all().from("url_statistics");
            ResultSet resultSet2 = null;
            resultSet2 = cassandraTemplate.getSession().execute(select_url_statistics);

//            domainList.add(i);
            //select_url_statistics.where(QueryBuilder.eq("domain", "www.dahe.cn"));
            //dayUpdateCount end
        } else {
            res.setResponse("data", null);
        }

        //Return an object with a list of chat messages and the next paging state
        //return new ChatPage(chats, serializedNewPagingState);


        res.setResponse("pageNum", pageNum);

        return res;
    }


}
