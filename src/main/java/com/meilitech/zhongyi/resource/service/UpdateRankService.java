package com.meilitech.zhongyi.resource.service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.ReadFailureException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.meilitech.zhongyi.resource.dao.ResourceDao;
import com.meilitech.zhongyi.resource.util.DateUtil;
import com.meilitech.zhongyi.resource.util.ResponseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sendy
 */
@Service
public class UpdateRankService {
    private static final Logger log = LoggerFactory.getLogger(UpdateRankService.class);

    @Autowired
    CassandraOperations cassandraTemplate;

    /**
     * 获取rank值并更新到resource表
     */
    public void getAndUpdateRank() {
        ResponseTemplate res = new ResponseTemplate();

        String startTime = DateUtil.getDate(8);
        String endTime = DateUtil.getDate(1);

        String select = "select domain,dayUpdateCount from  url_statistics  where  ymd>='" + startTime + "' and ymd<='" + endTime + "'  allow filtering ";
        ResultSet resultSet;
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
        } catch (ReadFailureException e) {
            res.setResultCode("db_err");
            e.printStackTrace();
            return;
        }
        Map<String,Object> map = new HashMap<>();
        for(Row row : resultSet){
            if (map.containsKey(row.getString(0))){
                Long temp = (Long)map.get(row.getString(0));
                map.put(row.getString(0),temp+row.getLong(1));
            }else {
                map.put(row.getString(0),row.getLong(1));
            }
        }

        for (String str : map.keySet()) {
            Select sct = QueryBuilder.select().all().from("resource");
            sct.where(QueryBuilder.eq("domain", str));
            sct.allowFiltering();
            sct.enableTracing();

            ResultSet rs;
            try {
                rs = cassandraTemplate.getSession().execute(sct);
                CassandraConverter converter = cassandraTemplate.getConverter();
                for (Row row : rs) {
                    ResourceDao chat = converter.read(ResourceDao.class, row);
                    String query1 = "delete from  resource  where rank=" + chat.getRank() + "  and resourceTaskId = '" + chat.getResourceTaskId() + "' and ymd= '" + chat.getYmd() +
                            "' and crawlerTime = '" + chat.getCrawlerTime().getTime() + "' and createTime = '" + chat.getCreateTime().toEpochMilli() + "' and publishTime = '" + chat.getPublishTime().getTime() +
                            "' and updateTime= '" + chat.getUpdateTime().getTime() + "' and resourceId = " + chat.getResourceId() +
                            " ;";
                    log.info(query1);
                    Long rank = (Long)map.get(str)/1000000;
                    if (rank > 100) {
                        rank = 99L;
                    } else if (rank < 0) {
                        rank = 0L;
                    }
                    cassandraTemplate.getSession().execute(query1);
                    String query2 ="INSERT INTO resource ( resourceid,ymd,resourcetaskid,url,urltype,"+
                            "domain,title,keywords,description,charset,"+
                            "crawlerTime,publishTime,categoryIds,country,"+
                            "createtime,rank,language,provider,status,updatetime,maxcrawlcount) VALUES("+
                            chat.getResourceId()+",'"+chat.getYmd()+"','"+chat.getResourceTaskId()+"','"+chat.getUrl()+
                            "'," +chat.getUrlType()+",'"+chat.getDomain()+"','"+chat.getTitle()+"','"+chat.getKeywords()+
                            "','"+chat.getDescription()+"','"+chat.getCharset()+"','"+chat.getCrawlerTime().getTime()+
                            "','"+chat.getPublishTime().getTime()+"','"+chat.getCategoryIds()+"','"+chat.getCountry()+
                            "','"+chat.getCreateTime().toEpochMilli()+"',"+ rank +",'"+chat.getLanguage()+
                            "','"+chat.getProvider()+"',"+chat.getStatus()+",'"+chat.getUpdateTime().getTime()+"',"+chat.getMaxCrawlCount()+");";
                    log.info(query2);
                    cassandraTemplate.getSession().execute(query2);
                }

            } catch (ReadFailureException e) {
                res.setResultCode("db_err");
                e.printStackTrace();
                return;
            }
        }
    }

}

