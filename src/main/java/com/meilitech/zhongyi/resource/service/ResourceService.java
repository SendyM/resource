package com.meilitech.zhongyi.resource.service;

import com.meilitech.zhongyi.resource.constants.SysError;
import com.meilitech.zhongyi.resource.dao.*;
import com.meilitech.zhongyi.resource.exception.UserException;
import com.meilitech.zhongyi.resource.util.FileUtil;
import com.meilitech.zhongyi.resource.util.RedissonUtil;
import com.meilitech.zhongyi.resource.util.ResponseTemplate;
import com.meilitech.zhongyi.resource.util.ToolsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ResourceService {
    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);
    private String[] contentColNameArr = {"url",
            "urlType",
            "domain",
            "referer",
            "referrer",
            "title",
            "keywords",
            "description",
            "charset",
            "crawlerStatus",
            "crawlerTime",
            "publishTIme",
            "categoryIds",
            "country",
            "createTime",
            "rank",
            "language",
            "provider",
            "status",
            "updateTime"};
    RedissonUtil redissonUtil = new RedissonUtil(RedissonUtil.BF_RESOURCE, true);

    ResourceService() {
        System.out.print(1);
    }

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UrlStatisticsRepository urlStatisticsRepository;

    @Autowired
    private ResourceDetailRepository resourceDetailRepository;

    public ResponseTemplate importFromFile(String filePath, String provider) {
        ResponseTemplate res = new ResponseTemplate();

        do {
            String sourceDataPath = filePath;
            File conentFile = new File(sourceDataPath);
            if (!conentFile.exists()) {
                res.setResultCode(SysError.SUCCESS);
                res.setResultMsg("文件不存在:" + sourceDataPath);
                break;
            }


            if (!FileUtil.decompress(conentFile)) {
                res.setResultCode(SysError.FILE_DECOMPRESS_ERR);
                res.setResultMsg("文件解压缩失败");
                break;
            }

            try {
                String[] contentColNameArrInput;
                File file = new File(sourceDataPath.replace(".tar.gz", "").replace(".gz", ""));
                // 读取文件，并且以utf-8的形式写出去
                BufferedReader bufread;
                String read;
                bufread = new BufferedReader(new FileReader(file));


                //解析第一行,内容列名
                if ((read = bufread.readLine()) != null) {
                    read = FileUtil.removeUTF8BOM(read);
                    //检查列名格式
                    contentColNameArrInput = read.split("\t");
                    Set<String> s1 = new HashSet<String>(Arrays.asList(contentColNameArr));
                    Set<String> s2 = new HashSet<String>(Arrays.asList(contentColNameArrInput));
                    s1.retainAll(s2);

                    String[] colArr = s1.toArray(new String[s1.size()]);

                    if (colArr.length != contentColNameArrInput.length) {
                        res.setResultCode(SysError.IMPORT_COL_ERR);
                        res.setResultMsg(String.format("存在列名命名不合法,标准列名为(%s)", String.join(",", contentColNameArr)));
                        break;
                    }
                } else {
                    res.setResultCode(SysError.IMPORT_CONTENT_EMPTY);
                    break;
                }

                int line = 0;
                int errLine = 0;
                Map<Integer, String> errRecord = new HashMap<Integer, String>();
                String resourceTaskId = ToolsUtil.getTaskId();
                while ((read = bufread.readLine()) != null) {
                    line++;
                    String[] item = read.split("\t");
                    if (item.length != contentColNameArrInput.length) {
                        errLine++;
                        errRecord.put(line, read);
                        continue;
                    }

                    Instant nowInstant = Instant.now();
                    LocalDate ymd = nowInstant.atZone(ZoneId.systemDefault()).toLocalDate();
                    ResourceDao resource = new ResourceDao();
                    resource.setResourceId(UUID.randomUUID());
                    resource.setResourceTaskId(resourceTaskId);
                    resource.setProvider(provider);
                    resource.setYmd(ymd);

                    ResourceDetailDao resourceDetail = new ResourceDetailDao();
                    resourceDetail.setResourceTaskId(resourceTaskId);
                    resourceDetail.setProvider(provider);

                    //String ymd = new SimpleDateFormat("yyyyMMdd");
                    resourceDetail.setYmd(ymd);

                    for (int i = 0; i < item.length; i++) {
                        if (i > contentColNameArrInput.length) {
                            log.warn(String.format("line content validate,content(%s),errNo:(%d)", read, SysError.IMPORT_COL_ERR));
                            break;
                        }

                        String colName = contentColNameArrInput[i];


                        switch (colName) {
                            case "url":
                                String sUrl = item[i];

                                if (!sUrl.matches("^(http?)://.*$") && !sUrl.matches("^(https?)://.*$")) {
                                    sUrl = "http://" + sUrl;
                                }

                                try {
                                    URL url = new URL(sUrl);

                                    String domain = url.getHost().toString();
                                    if (!domain.matches("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$")) {
                                        log.debug("ignore:" + sUrl);
                                        continue;
                                    }
                                    resource.setDomain(domain);
                                    resource.setUrl(sUrl);

                                    resourceDetail.setDomain(domain);
                                    resourceDetail.setUrl(sUrl);
                                } catch (MalformedURLException e) {
                                    log.warn(String.format("ignore:(%s)", read));
                                    errLine++;
                                    errRecord.put(line, read);
                                    continue;
                                }
                                break;
                            case "referrer":
                            case "referer":
                                resourceDetail.setReferer(item[i]);
                                break;
                            case "urlType":
                                resource.setUrlType(Integer.parseInt("0" + item[i]));
                                break;
                            case "title":
                                resource.setTitle(item[i]);
                                break;
                            case "keywords":
                                resource.setKeywords(item[i]);
                                break;
                            case "description":
                                resource.setDescription(item[i]);
                                break;
                            case "charset":
                                resource.setCharset(item[i].toLowerCase());
                                break;
                            case "crawlerStatus":
                                resourceDetail.setCrawlerStatus(Integer.parseInt(item[i]));
                                break;
                            case "crawlerTime":
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date parsedDate = dateFormat.parse(item[i]);
                                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                                    resource.setCrawlerTime(timestamp);
                                } catch (Exception e) { //this generic but you can control another types of exception
                                    // look the origin of excption
                                    log.warn(String.format("ignore:(%s)", read));
                                    errLine++;
                                    errRecord.put(line, read);
                                    continue;
                                }
                                break;
                            case "categoryIds":
                                resource.setCategoryIds(item[i]);
                                break;
                            case "country":
                                resource.setCountry(item[i]);
                                break;
                            case "createTime":
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date parsedDate = dateFormat.parse(item[i]);
                                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                                    resource.setCreateTime(timestamp.toInstant());
                                    resourceDetail.setCreateTime(timestamp.toInstant());
                                } catch (Exception e) { //this generic but you can control another types of exception
                                    // look the origin of excption
                                    log.warn(String.format("ignore:(%s)", read));
                                    errLine++;
                                    errRecord.put(line, read);
                                    continue;
                                }
                                break;
                            case "rank":
                                resource.setRank(Integer.parseInt(item[i]));
                                break;
                            case "language":
                                resource.setLanguage(item[i].toLowerCase());
                                break;
                            case "status":
                                resource.setStatus(Integer.parseInt(item[i]));
                                break;
                            case "updateTime":
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                                    Date parsedDate = dateFormat.parse(item[i]);
                                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                                    resource.setUpdateTime(timestamp);
                                } catch (Exception e) { //this generic but you can control another types of exception
                                    // look the origin of excption
                                    log.warn(String.format("ignore:(%s)", read));
                                    errLine++;
                                    errRecord.put(line, read);
                                    continue;
                                }
                                break;
                        }
                    }

                    if (resource.getUrl() == null || resource.getUrl().isEmpty()) {
                        log.warn(String.format("ignore:(%s)", read));
                        errLine++;
                        errRecord.put(line, read);
                        continue;
                    }

                    if (redissonUtil.contains(resourceDetail.getUrl())) {
                        log.warn(String.format("ignore:(%s)", read));
                        errLine++;
                        errRecord.put(line, read);
                        continue;
                    }


                    resource.setUpdateTime(new Date());
                    if (resource.getCreateTime() == null) {
                        resource.setCreateTime(Instant.now());
                    }

                    if (resource.getCrawlerTime() == null) {
                        resource.setCrawlerTime(new Date(0));
                    }

                    if (resource.getPublishTime() == null) {
                        resource.setPublishTime(new Date(0));
                    }

                    /*
                    if (resource.getUrlType() == 0) {
                        String testUrl = resource.getUrl().replace("https://", "").replace("http://", "").replace("/", "");
                        if (testUrl.equals(resource.getDomain())) {
                            resource.setUrlType(ResourceDao.UrlType.DOMAIN.ordinal());
                        } else {
                            //resource.setUrlType(ResourceDao.UrlType.LIST.ordinal());
                        }
                    }*/

                    //从采集中心来的，默认为Domain类型
                    if(resource.getProvider() == ResourceDao.Provider.NEWSPRIDER.toString()){
                        resource.setUrlType(ResourceDao.UrlType.DOMAIN.getUrlType());
                        resource.setUrl(resource.getDomain());
                    }else{
                        //从调度中心来的如果文件url字段值为url型（eg:www.baidu.com/page/xx等）则为List型，否则为Domain型
                        String url = resource.getUrl();
                        if(url.matches("^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)+[\\w-_/?&=#%:\\.]{1,}$")){
                            resource.setUrlType(ResourceDao.UrlType.LIST.getUrlType());
                        }else{
                            resource.setUrlType(ResourceDao.UrlType.DOMAIN.getUrlType());
                        }
                    }

                    if (resource.getRank() == 0) {
                        resource.setRank((int) (Math.random() * 80));
                    }

                    if (resource.getLanguage() == null) {
                        resource.setLanguage("");
                    }

                    if(resource.getKeywords() == null){
                        resource.setKeywords("");
                    }

                    if(resource.getDescription() == null){
                        resource.setDescription("");
                    }

                    if(resource.getCharset() == null){
                        resource.setCharset("");
                    }

                    if(resource.getCategoryIds() == null){
                        resource.setCategoryIds("");
                    }

                    if(resource.getCountry() == null){
                        resource.setCountry("");
                    }

                    //第一次插入存值0
                    resource.setDayUpdateCount(0);
                    resource.setStatus(0);

                    if(resource.getMaxCrawlCount() == null){
                        resource.setMaxCrawlCount(0);
                    }

                    //保存域名以及
                    if ((resource.getUrlType() == ResourceDao.UrlType.DOMAIN.getUrlType() || resource.getUrlType() == ResourceDao.UrlType.LIST.getUrlType())
                            /*&& !redissonUtil.contains(resource.getUrl())*/) {
                        try {
                            resourceRepository.save(resource);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    resourceDetailRepository.save(resourceDetail);
                    urlStatisticsRepository.updateCounter(resource.getDomain(), new SimpleDateFormat("yyyy-MM-dd").format(Date.from(resource.getCreateTime())), resource.getProvider());

                    redissonUtil.add(resourceDetail.getUrl());

                }
                bufread.close();

                if (errRecord.isEmpty()) {
                    res.setResultCode(SysError.SUCCESS);
                    res.setResponse("resourceTaskId", resourceTaskId);
                    res.setResultMsg(String.format("更新完成(成功:%d条,失败:%d条)", line - errLine, errLine));
                } else {
                    res.setResponse("resourceTaskId", resourceTaskId);
                    res.setResultCode(SysError.PART_SUCCESS);
                    res.setResultMsg(errRecord.toString());
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


    public  List<ResourceDao> getResourceByDomain(String domain){
      return   resourceRepository.getResourceByDomain(domain);
    }
}

