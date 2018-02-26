package com.meilitech.zhongyi.resource.task;

import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.meilitech.zhongyi.resource.dao.ResourceDao;
import com.meilitech.zhongyi.resource.dao.ResourceRepository;
import com.meilitech.zhongyi.resource.dao.UrlDao;
import com.meilitech.zhongyi.resource.dao.UrlRepository;
import com.meilitech.zhongyi.resource.exception.UserException;
import com.meilitech.zhongyi.resource.service.ResourceService;
import com.meilitech.zhongyi.resource.service.UrlService;
import com.meilitech.zhongyi.resource.util.FileUtil;
import com.meilitech.zhongyi.resource.util.RedissonUtil;
import com.meilitech.zhongyi.resource.util.ToolsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FtpParseTasks {
    private static final Logger log = LoggerFactory.getLogger(FtpParseTasks.class);

    @Autowired
    ResourceRepository resourceRepository;

    RedissonUtil redissonUtil;

    @Autowired
    ResourceService resourceService;

    @Autowired
    Environment env;


    /**
     * 定时读写文件，一次只读一个文件
     */
    @Scheduled(fixedDelay = 5000)
    public void parse() {
        redissonUtil = new RedissonUtil(RedissonUtil.BF_RESOURCE, true);
        Map<String, Object> map = new HashMap<String, Object>();

        File dir = new File(env.getProperty("resource.data.path")+"xdth/");
        File[] filesList = dir.listFiles();
        if(filesList == null){
            log.info("no file,skip:" + dir.getAbsolutePath());
            return;
        }
        for (File file : filesList) {
            if (!file.isFile()) continue;
            do {
                if (!file.getName().matches(".*\\.gz")) {
                    log.debug("ignore:" + file.getAbsolutePath());
                    continue;
                }

                try {
                    resourceService.importFromFile(file.getAbsolutePath(), ResourceDao.Provider.NEWSPRIDER.toString());
                } catch (UserException e) {
                    File newFile = new File(file.getAbsoluteFile() + ".fail");
                    file.renameTo(newFile);
                    log.warn(file.getAbsoluteFile() + ".fail");
                    continue;
                }

                File newFile = new File(file.getAbsoluteFile() + ".done");
                file.renameTo(newFile);
                return;
            } while (false);
        }
    }

//    public boolean saveData(File file) {
//        String[] contentColNameArr = {"url",
//                "urlType",
//                "domain",
//                "referer",
//                "title",
//                "keywords",
//                "description",
//                "charset",
//                "crawlerStatus",
//                "crawlerTime",
//                "publishTIme",
//                "categoryIds",
//                "country",
//                "createTime",
//                "rank",
//                "language",
//                "provider",
//                "referrer",
//                "status",
//                "updateTime"};
//        Map<String, String> res = new HashMap<>();
//        res.put("err", "ok");
//        res.put("errMsg", "");
//
//
//        try {
//            String[] contentColNameArrInput;
//            // 读取文件，并且以utf-8的形式写出去
//            BufferedReader bufread;
//            String read;
//            bufread = new BufferedReader(new FileReader(file));
//
//
//            //读出第一行
//            String re = null;
//            if ((read = bufread.readLine()) != null) {
//                read = FileUtil.removeUTF8BOM(read);
//                //检查列名格式
//                re = (read.indexOf("\t") > 0) ? "\t" : ",";
//                contentColNameArrInput = read.split(re);
//                Set<String> s1 = new HashSet<String>(Arrays.asList(contentColNameArr));
//                Set<String> s2 = new HashSet<String>(Arrays.asList(contentColNameArrInput));
//                s1.retainAll(s2);
//
//                String[] result = s1.toArray(new String[s1.size()]);
//
//                if (result.length != contentColNameArrInput.length) {
//                    log.warn(String.format("存在列名命名不合法,标准列名为(%s)", String.join(",", contentColNameArr)));
//                    return false;
//                }
//            } else {
//                log.warn("内容为空");
//                return false;
//            }
//
//            int line = 0;
//            int errLine = 0;
//            String resourceTaskId = ToolsUtil.getTaskId();
//            Map<Integer, String> errRecord = new HashMap<Integer, String>();
//            while ((read = bufread.readLine()) != null) {
//                line++;
//                String[] item = read.split(re);
//                if (item.length != contentColNameArrInput.length) {
//                    errLine++;
//                    errRecord.put(line, read);
//                    continue;
//                }
//
//                ResourceDao resource = new ResourceDao();
//                resource.setResourceId(UUID.randomUUID());
//                resource.setResourceTaskId(resourceTaskId);
//                resource.setProvider(ResourceDao.Provider.NEWSPRIDER.toString());
//
//                for (int i = 0; i < item.length; i++) {
//                    if (i > contentColNameArrInput.length) {
//                        break;
//                    }
//                    String colName = contentColNameArrInput[i];
//                    switch (colName) {
//                        case "url":
//                            String sUrl = item[i];
//
//                            if (!sUrl.matches("^(http?)://.*$") && !sUrl.matches("^(https?)://.*$")) {
//                                sUrl = "http://" + sUrl;
//                            }
//
//                            URL url = new URL(sUrl);
//                            resource.setDomain(url.getHost().toString());
//                            resource.setUrl(sUrl);
//                            break;
//                        case "referrer":
//                        case "referer":
//                            resource.setReferer(item[i]);
//                            break;
//                        case "urlType":
//                            resource.setTitle(item[i]);
//                            break;
//                        case "title":
//                            resource.setTitle(item[i]);
//                            break;
//                        case "keywords":
//                            resource.setKeywords(item[i]);
//                            break;
//                        case "description":
//                            resource.setDescription(item[i]);
//                            break;
//                        case "charset":
//                            resource.setCharset(item[i]);
//                            break;
//                        case "crawlerStatus":
//                            resource.setCrawlerStatus(Integer.parseInt(item[i]));
//                            break;
//                        case "crawlerTime":
//                            try {
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                                Date parsedDate = dateFormat.parse(item[i]);
//                                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//                                resource.setCrawlerTime(timestamp);
//                            } catch (Exception e) { //this generic but you can control another types of exception
//                                // look the origin of excption
//                            }
//                            break;
//                        case "categoryIds":
//                            resource.setCategoryIds(item[i]);
//                            break;
//                        case "country":
//                            resource.setCountry(item[i]);
//                            break;
//                        case "createTime":
//                            try {
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                                Date parsedDate = dateFormat.parse(item[i]);
//                                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//                                //resource.setCreateTime(timestamp);
//                            } catch (Exception e) { //this generic but you can control another types of exception
//                                // look the origin of excption
//                            }
//                            break;
//                        case "rank":
//                            resource.setRank(Integer.parseInt(item[i]));
//                            break;
//                        case "language":
//                            resource.setLanguage(item[i]);
//                            break;
//                        case "status":
//                            resource.setStatus(Integer.parseInt(item[i]));
//                            break;
//                        case "updateTime":
//                            try {
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                                Date parsedDate = dateFormat.parse(item[i]);
//                                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//                                resource.setUpdateTime(timestamp);
//                            } catch (Exception e) { //this generic but you can control another types of exception
//                                // look the origin of excption
//                            }
//                            break;
//                    }
//                }
//
//                if (resource.getCreateTime() == null) {
//                    Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
//                    resource.setUpdateTime(timestamp);
//                }
//
//                if (resource.getUrl().isEmpty()) {
//                    continue;
//                }
//
//                if (redissonUtil.contains(resource.getUrl())) {
//                    log.debug("ignore" + resource.getUrl());
//                    continue;
//                } else {
//                    redissonUtil.add(resource.getUrl());
//                }
//
//
//                Date date = new Date();
//                Timestamp now = new Timestamp(date.getTime());
//                resource.setUpdateTime(now);
//                resourceRepository.save(resource);
//
//            }
//            System.out.println(read);
//
//            bufread.close();
//
//            if (errRecord.isEmpty()) {
//                log.info(String.format("跟新完成(成功:%d条,失败:%d条)", line - errLine, errLine));
//            } else {
//                log.warn(errRecord.toString());
//            }
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//            return false;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }
}
