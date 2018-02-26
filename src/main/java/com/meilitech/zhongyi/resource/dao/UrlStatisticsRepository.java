
package com.meilitech.zhongyi.resource.dao;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;


public interface UrlStatisticsRepository extends CrudRepository<UrlStatisticsDao, Long> {

    /**
     *
     * @return
     */
    //@Query("SELECT * from resource_task WHERE provider in ('task_center','data_01','inner_spider_news_01')  AND ymd=:ymd limit 1000")
    //List<ResourceTaskDao> getData(@Param("ymd") String ymd);

    @Query("SELECT * from url_statistics  limit 1000")
    List<UrlStatisticsDao> getData();


    @Query("update url_statistics set dayUpdateCount=dayUpdateCount+1 where domain = :domain and ymd=:ymd and provider = :provider")
    void updateCounter(@Param("domain") String domain, @Param("ymd") String ymd,@Param("provider") String provider);
}