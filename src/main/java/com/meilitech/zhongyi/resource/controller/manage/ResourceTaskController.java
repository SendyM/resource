package com.meilitech.zhongyi.resource.controller.manage;

import com.meilitech.zhongyi.resource.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhongyi
 */
@RequestMapping("/manage")
@Controller
@EnableAutoConfiguration
public class ResourceTaskController {
    @Autowired
    UrlStatisticsRepository urlStatisticsRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ResourceStatisticsRepository resourceStatisticsRepository;

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

        for (UrlStatisticsDao aTaskList : taskList) {
            String[] item = new String[5];
            item[0] = aTaskList.getProvider();
            item[1] = aTaskList.getDomain();
            item[2] = aTaskList.getResourceTaskId();
            item[3] = aTaskList.getYmd().toString();
            item[4] = aTaskList.getDayUpdateCount().toString();
            data.add( item );
        }
        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        res.put("data", data);
        return res;
    }

    /**
     *查询域名每天的更新量
     */
    @RequestMapping("/api/resource")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiResource(Model model) {
        /* List<ResourceDao> domainList = resourceRepository.getData(50); */

        Date d1 = null;
        Date d2 = null;
        try {
            //定义起始日期
            d1 = new SimpleDateFormat("yyyyMMdd").parse("20170115");
            //定义结束日期
            d2 = new Date();
        } catch (Exception e) {
            e.printStackTrace();
        }


        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        ArrayList<String[]> data = new ArrayList<String[]>();
        //定义日期实例
        Calendar dd = Calendar.getInstance();
        //设置日期起始时间
        dd.setTime(d1);
        //判断是否到结束日期
        while (dd.getTime().before(d2)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String yyyyMMdd = sdf.format(dd.getTime());

            //System.out.println(yyyyMMdd);输出日期结果
            String[] line = new String[3];
            List<ResourceStatisticsDao> data2 = resourceStatisticsRepository.getListByDay(yyyyMMdd);

            dd.add(Calendar.DATE, 1);
            if (data2.get(0).getYmd() == null) {
                continue;
            }
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


    @RequestMapping("/api/resource/detail")
    @ResponseBody
    public HashMap<String, ArrayList<String[]>> apiResourceDetail(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {

        String ymd = reqMap.getOrDefault("ymd", "").toString();
        if (ymd.isEmpty()) {
            return null;
        }

        List<ResourceDao> domainList = resourceRepository.getDetailByDay(ymd);


        HashMap<String, ArrayList<String[]>> res = new HashMap<>();
        ArrayList<String[]> data = new ArrayList<String[]>();


        for (ResourceDao aDomainList : domainList) {
            String[] line = new String[2];
            line[0] = aDomainList.getDomain();
            line[1] = aDomainList.getYmd().toString();
            data.add( line );
        }

        res.put("data", data);
        return res;
    }
}
