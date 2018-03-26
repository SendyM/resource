package com.meilitech.zhongyi.resource.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.ReadFailureException;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.meilitech.zhongyi.resource.constants.SysError;
import com.meilitech.zhongyi.resource.dao.*;
import com.meilitech.zhongyi.resource.exception.UserException;
import com.meilitech.zhongyi.resource.service.ResourceService;
import com.meilitech.zhongyi.resource.util.DateUtil;
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

import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 更新域名抓取url数量阈值
     *
     * @param reqMap     Map
     * @param httpEntity HttpEntity
     * @return ResponseTemplate
     */
    @PostMapping
    @RequestMapping("/updateMaxCrawlCount")
    public ResponseTemplate updateMaxCrawlCount(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {
        ResponseTemplate res = new ResponseTemplate();
        JSONObject data = (JSONObject) JSON.parse(reqMap.getOrDefault("data", new JSONObject()).toString());
        JSONObject body = (JSONObject) data.getOrDefault("body", new JSONObject());
        JSONObject request = (JSONObject) body.getOrDefault("request", new JSONObject());

        String domain = (String) request.get("domain");
        String provider = (String) request.get("provider");

        int maxCrawlCount = Integer.valueOf((String) request.getOrDefault("maxCrawlCount", "20000"));


        if (domain == null || domain.isEmpty()) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }
        if (provider.equals(ResourceDao.Provider.TASKCENTER.toString())) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }

        Select select = QueryBuilder.select().all().from("resource");
        select.where(QueryBuilder.eq("domain", domain));
        select.where(QueryBuilder.eq("provider", provider));
        select.allowFiltering();
        select.enableTracing();

        ResultSet resultSet;
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
            CassandraConverter converter = cassandraTemplate.getConverter();
            for (Row row : resultSet) {
                ResourceDao chat = converter.read(ResourceDao.class, row);
                System.out.println(chat.getCrawlerTime().getTime());
                String query = "UPDATE resource SET maxcrawlcount =" + maxCrawlCount + "  WHERE rank=" + chat.getRank() + "  and resourceTaskId = '" + chat.getResourceTaskId() + "' and ymd= '" + chat.getYmd() +
                        "' and crawlerTime = '" + chat.getCrawlerTime().getTime() + "' and createTime = '" + chat.getCreateTime().toEpochMilli() + "' and publishTime = '" + chat.getPublishTime().getTime() +
                        "' and updateTime= '" + chat.getPublishTime().getTime() + "' and resourceId = " + chat.getResourceId() + ";";
                cassandraTemplate.getSession().execute(query);
            }

        } catch (ReadFailureException e) {
            res.setResultCode("db_err");
            e.printStackTrace();
            return res;
        }
        return res;
    }

    /**
     * 获取rank值
     *
     * @param reqMap Map
     * @return ResponseTemplate
     */
    @PostMapping
    @RequestMapping("/getRank")
    public ResponseTemplate getRank(@RequestParam Map<String, Object> reqMap) {
        ResponseTemplate res = new ResponseTemplate();
        JSONObject data = (JSONObject) JSON.parse(reqMap.getOrDefault("data", new JSONObject()).toString());
        JSONObject body = (JSONObject) data.getOrDefault("body", new JSONObject());
        JSONObject request = (JSONObject) body.getOrDefault("request", new JSONObject());

        String domain = (String) request.get("domain");
        String provider = (String) request.get("provider");

        if (domain == null || domain.isEmpty()) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }
        if (provider.equals(ResourceDao.Provider.TASKCENTER.toString())) {
            res.setResultCode(SysError.IMPORT_PROVIDER_ERR);
            return res;
        }

        String startTime = DateUtil.getDate(8);
        String endTime = DateUtil.getDate(1);

        String select = "SELECT  dayUpdateCount FROM  url_statistics  WHERE provider='HH'and ymd>='" + startTime + "' and ymd<='" + endTime + "' and domain='www.baidu.com'  ALLOW FILTERING ";
        ResultSet resultSet;
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
        } catch (ReadFailureException e) {
            res.setResultCode("db_err");
            e.printStackTrace();
            return res;
        }
        Long dayUpdateCount;
        Long sum = 0L;
        for (Row row : resultSet) {
            dayUpdateCount = row.getLong("dayUpdateCount");
            sum = sum + dayUpdateCount;
        }
        Long rank = sum / 1000000;
        if (rank > 100) {
            rank = 99L;
        } else if (rank < 0) {
            rank = 0L;
        }
        //todo
        res.setResponse("rank", rank);

        return res;
    }

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
        String urlType = (String) request.getOrDefault("urlType", "");
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
           /*
            * bug修改：分页应该放到sql拼接结束的地方
            if (page != null && !page.isEmpty()) {
                //select.setPagingState(PagingState.fromString(page));
                //break;
            }
            */

            if (!resourceTaskId.isEmpty()) {
                select.where(QueryBuilder.eq("resourceTaskId", resourceTaskId));
                break;
            }

            if (!domain.isEmpty() && !urlType.isEmpty()) {
                select.where(QueryBuilder.eq("domain", domain));
                select.where(QueryBuilder.eq("urlType", Integer.valueOf(urlType)));
                break;
            }

            if (!domain.isEmpty()) {
                if (isLike.equals("1")) {
                    select.where(QueryBuilder.like("domain", "%" + domain + "%"));
                    break;
                } else {
                    select.where(QueryBuilder.eq("domain", domain));
                }
            } else {
                int rankMin = 0;
                int rankMax = 0;

                rankMin = Integer.valueOf((String) request.getOrDefault("rankMin", "0"));
                rankMax = Integer.valueOf((String) request.getOrDefault("rankMax", "99"));

                ArrayList rankList = new ArrayList();
                for (int i = rankMin; i <= rankMax; i++) rankList.add(i);
                select.where(QueryBuilder.in("rank", rankList));
            }


            String crawlerTimeStartParams = (String) request.getOrDefault("crawlerTimeStart", "");
            String crawlerTimeEndParams = (String) request.getOrDefault("crawlerTimeEnd", "");

            if (!crawlerTimeStartParams.isEmpty() && !crawlerTimeEndParams.isEmpty()) {
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

            if (!publishTimeStartParams.isEmpty() && !publishTimeEndParams.isEmpty()) {
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

            if (!lastUpdateTimeStartParams.isEmpty() && !lastUpdateTimeEndParams.isEmpty()) {
                long lastUpdateTimeStart;
                long lastUpdateTimeEnd;
                try {
                    lastUpdateTimeStart = dateFormat.parse(lastUpdateTimeStartParams).getTime();
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
            if (!createTimeStartParams.isEmpty() && !createTimeEndParams.isEmpty()) {
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

            String categoryId = (String) request.getOrDefault("categoryId", "");
            if (!categoryId.isEmpty()) {
                select.where(QueryBuilder.eq("categoryIds", categoryId));
            }
        } while (false);

        select.setFetchSize(pageNum);
        select.allowFiltering();
        select.enableTracing();
        if (page != null && !page.isEmpty()) {
            select.setPagingState(PagingState.fromString(page));
        }

        log.info(select.toString());


        //Used to map rows to Chat domain objects
        CassandraConverter converter = cassandraTemplate.getConverter();

        //Execute the query
        ResultSet resultSet;
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

            List<ResourceDaoVo> rList = new ArrayList<>(pageNum);
            for (Row row : resultSet) {
                //Convert rows to chat objects
                try {
                    ResourceDaoVo chat = converter.read(ResourceDaoVo.class, row);

                    chat.setCategoryId(chat.getCategoryIds());

                    // domainList.add(chat.getDomain());
                    //日更量
                    /*
                    String query = "select dayUpdateCount from url_statistics where domain='"+chat.getDomain()+"'  and  ymd='"+chat.getYmd() +"'and  provider = '"+chat.getProvider()+"'";
                    ResultSet rs = cassandraTemplate.getSession().execute(query);
                    int dayUpdateCount = 0;
                    for (Row rw : rs) {
                        dayUpdateCount = rw.getInt("dayUpdateCount");
                    }
                    chat.setDayUpdateCount(dayUpdateCount);
                    */

                    rList.add(chat);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                //If we can't move to the next row without fetching we break
                if (--remaining == 0) {
                    break;
                }
            }

            //Serialise the next paging state
            String serializedNewPagingState = newPagingState != null ? newPagingState.toString() : null;
            res.setResponse("pageNum", rList.size());
            res.setResponse("data", rList);
            if (serializedNewPagingState != null) {
                res.setResponse("resultSign", serializedNewPagingState);
            }
        } else {
            res.setResponse("data", null);
        }

        //Return an object with a list of chat messages and the next paging state
        //return new ChatPage(chats, serializedNewPagingState);

        return res;
    }


    //用于采集征文推送完成
    @RequestMapping("/contentEndApi")
    @ResponseBody
    public String contentEndApi(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {

        Map<String, Object> map = new HashMap<String, Object>();

        File dir = new File(env.getProperty("resource.data.path") + "xdth_text_domain/");
        File[] filesList = dir.listFiles();
        if (filesList == null) {
            log.info("no file,skip:" + dir.getAbsolutePath());
            return "error";
        }

        for (File file : filesList) {
            if (!file.isFile()) continue;
            if (file.getName().matches(".*\\.done") || file.getName().matches(".*\\.fail") || file.getName().matches(".*\\.dict")) {
                log.debug("ignore:" + file.getAbsolutePath());
                continue;
            }

            do {
                try {
                    doCategory(file.getAbsolutePath());
                } catch (UserException e) {
                    File newFile = new File(file.getAbsoluteFile() + ".fail");
                    file.renameTo(newFile);
                    log.warn(file.getAbsoluteFile() + ".fail");
                    continue;
                } catch (Exception e) {
                    File newFile = new File(file.getAbsoluteFile() + ".fail");
                    file.renameTo(newFile);
                    log.warn(file.getAbsoluteFile() + ".fail");
                    continue;
                }

                File newFile = new File(file.getAbsoluteFile() + ".done");
                file.renameTo(newFile);
                return "success";
            } while (false);
        }
        return "success";
    }

    private ResponseTemplate doCategory(String filePath) {
        ResponseTemplate res = new ResponseTemplate();

        do {
            String sourceDataPath = filePath;
            File conentFile = new File(sourceDataPath);
            if (!conentFile.exists()) {
                res.setResultCode(SysError.SUCCESS);
                res.setResultMsg("文件不存在:" + sourceDataPath);
                break;
            }

            try {
                File file = new File(sourceDataPath);
                filePath = file.getAbsolutePath();
                if (file.exists()) {
                    String pyPath = env.getProperty("python.command.path") + " " + env.getProperty("category.command.path") + "/main_run.py zyyt test_one "+filePath;
                    //提取特征资料


                    //预测网站类型
                    Process p = Runtime.getRuntime().exec(pyPath);
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String ret = "";
                    String input = null;
                    while ((input = in.readLine()) != null) {
                        ret = ret + input;
                    }



                    String domain = file.getName();

                    System.out.println("pythoy------------ output is : " + ret);
                    log.info(domain,":python run result:", res);



                    List<ResourceDao> list = resourceService.getResourceByDomain(domain);
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            ResourceDao resourceDao = list.get(i);

                            if (ret.contains("ok 1")) {
                                resourceDao.setCategoryIds("1");
                                resourceRepository.save(resourceDao);
                            } else if (ret.contains("ok -1")) {
                                resourceDao.setCategoryIds("-1");
                                resourceRepository.save(resourceDao);
                            } else if (ret.contains("ok 0")) {
                                resourceDao.setCategoryIds("0");
                                resourceRepository.save(resourceDao);
                            }
                        }

                    }
                } else {
                    log.warn(filePath + ", not exists!");
                }

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                throw new UserException(SysError.IMPORT_ERR, ex.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new UserException(SysError.IMPORT_ERR, ex.getMessage());
            }

            return res;
        } while (false);

        return res;
    }
}
