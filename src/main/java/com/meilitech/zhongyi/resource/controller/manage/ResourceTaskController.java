package com.meilitech.zhongyi.resource.controller.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.ReadFailureException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.meilitech.zhongyi.resource.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/manage")
@Controller
@EnableAutoConfiguration
public class ResourceTaskController {
    @Autowired
    UrlStatisticsRepository urlStatisticsRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ResourceDetailRepository resourceDetailRepository;

    @Autowired
    ResourceStatisticsRepository resourceStatisticsRepository;

    @Autowired
    CassandraOperations cassandraTemplate;

    @RequestMapping("/task")
    public String task(Model model) {
        model.addAttribute("key", "value");
        return "manage/task";
    }

    @RequestMapping("/api/task")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiTask() {


        List<UrlStatisticsDao> taskList = urlStatisticsRepository.getData();

        ArrayList<String[]> data = new ArrayList<String[]>();

        for (int i = 0; i < taskList.size(); i++) {
            String[] item = new String[5];
            item[0] = taskList.get(i).getProvider();
            item[1] = taskList.get(i).getDomain();
            item[2] = taskList.get(i).getResourceTaskId();
            item[3] = taskList.get(i).getYmd().toString();
            item[4] = taskList.get(i).getDayUpdateCount().toString();
            data.add(item);
        }
        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        res.put("data", data);
        return res;
    }

    /*
     查询域名每天的更新量
     */
    @RequestMapping("/api/resource")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiResource(Model model) {
        //List<ResourceDao> domainList = resourceRepository.getData(50);


        Date d1 = null;
        Date d2 = null;
        try {
            d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-26");//定义起始日期
            d2 = new Date();//定义结束日期
        } catch (Exception e) {
            e.printStackTrace();
        }


        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        ArrayList<String[]> data = new ArrayList<String[]>();

        Calendar dd = Calendar.getInstance();//定义日期实例

        dd.setTime(d1);//设置日期起始时间

        while (dd.getTime().before(d2)) {//判断是否到结束日期

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String yyyyMMdd = sdf.format(dd.getTime());

            //System.out.println(yyyyMMdd);//输出日期结果
            String[] line = new String[4];
            List<ResourceStatisticsDao> data2 = resourceStatisticsRepository.getListByDay(yyyyMMdd);

            dd.add(Calendar.DATE, 1);
            if(((ArrayList) data2).size()==0)continue;
            if (data2.get(0).getYmd() == null) continue;
            line[0] = data2.get(0).getYmd().toString();
            line[1] = data2.get(0).getDayUpdateCount().toString();
            line[2] = "<a href='/resource/detail?ymd=" + line[0] + "'>点击查看</a>";
            line[3] = data2.get(0).getProvider();
            data.add(line);
            //data.addAll(data2.)
        }

        res.put("data", data);
        return res;
    }


    /*
     查询每天需要审核的域名
     */
    @RequestMapping("/api/domain")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiDomain(Model model) {
        Select select = QueryBuilder.select().all().from("resource");
        select.where(QueryBuilder.eq("categoryIds", "1"));
        select.where(QueryBuilder.eq("status", 0));
        select.limit(100);
        select.allowFiltering();
        select.enableTracing();

        ResultSet resultSet = null;
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
        } catch (ReadFailureException e) {
            e.printStackTrace();
        }

        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        ArrayList<String[]> data = new ArrayList<String[]>();
        if (resultSet != null) {
            CassandraConverter converter = cassandraTemplate.getConverter();
            int remaining = resultSet.getAvailableWithoutFetching();
            int index = 0;

            for (Row row : resultSet) {
                try {
                    ResourceDao resource = converter.read(ResourceDao.class, row);
                    String[] line = new String[6];
                    line[0] = resource.getYmd().toString();
                    line[1] = resource.getDomain();
                    line[2] = resource.getUrl();
                    //line[3] = resource.getCategoryIds() == "1" ? "新闻" : "其他";
                    line[3] = "新闻";
                    //line[4] = String.valueOf(resource.getStatus()) == "0" ? "未审核" : "已审核";
                    //line[4] = "未审核";
                    line[4] = "<button type='button' class='btn btn-primary' data-resourceId='" +resource.getResourceId()+ "' data-index= '"+index+"'>审核</button>" ;
                    data.add(line);
                    index++;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        res.put("data", data);
        return res;
    }

    /*
     *审核域名
     */
    @RequestMapping("/api/domain/publish")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiDomainPublish(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {
        UUID resourceId = UUID.fromString((String) reqMap.getOrDefault("resourceId", 0));

        Select select = QueryBuilder.select().all().from("resource");
        select.where(QueryBuilder.eq("resourceId", resourceId));
        select.where(QueryBuilder.eq("status", 0));
        select.allowFiltering();
        select.enableTracing();

        ResultSet resultSet = null;
        try {
            resultSet = cassandraTemplate.getSession().execute(select);
        } catch (ReadFailureException e) {
            e.printStackTrace();
        }

        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        if (resultSet != null) {
            CassandraConverter converter = cassandraTemplate.getConverter();

            for (Row row : resultSet) {
                try {
                    ResourceDao resource = converter.read(ResourceDao.class, row);

                    String query = "update resource set status = 1  where rank=" + resource.getRank() + "  and resourceTaskId = '" + resource.getResourceTaskId() + "' and ymd= '" + resource.getYmd() +
                            "' and crawlerTime = '" + resource.getCrawlerTime().getTime() + "' and createTime = '" + resource.getCreateTime().toEpochMilli() + "' and publishTime = '" + resource.getPublishTime().getTime() +
                            "' and updateTime= '" + resource.getUpdateTime().getTime() + "' and resourceId = " + resource.getResourceId() +
                            " ;";
                    cassandraTemplate.getSession().execute(query);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }

    @RequestMapping("/api/resource/detail")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiResourceDetail(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {

        String ymd = reqMap.getOrDefault("ymd", "").toString();
        if (ymd.isEmpty()) {
            return null;
        }

        List<ResourceDetailDao> domainList = resourceDetailRepository.getDetailByDay(ymd);


        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        ArrayList<String[]> data = new ArrayList<String[]>();


        for (int i = 0; i < domainList.size(); i++) {
            String[] line = new String[2];
            line[0] = domainList.get(i).getDomain();
            line[1] = domainList.get(i).getYmd().toString();
            data.add(line);
        }

        res.put("data", data);
        return res;
    }


    //测试更新CategoryIds用
    @RequestMapping("/api/resource/testCategoryIds")
    @ResponseBody
    public String d3(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {

        List<ResourceDao>  list=resourceRepository.getResourceByDomain("blog.sina.com.cn");
       // list = resourceRepository.getData(3);
        if(list!=null &&list.size()>0){
            for (int i=0;i<list.size();i++){
                ResourceDao resourceDao = list.get(i);

                try {
                   // resourceRepository.updateResourceCategoryIds("1",  resourceDao.getRank(),  resourceDao.getResourceTaskId(), resourceDao.getYmd().toString(), resourceDao.getCrawlerTime().toString(),  resourceDao.getCreateTime().toString(), resourceDao.getPublishTime().toString(), resourceDao.getUpdateTime().toString(), resourceDao.getResourceId().toString());
                    resourceDao.setCategoryIds("0");
                    resourceRepository.save(resourceDao);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

        }


        return "success";
    }
}
