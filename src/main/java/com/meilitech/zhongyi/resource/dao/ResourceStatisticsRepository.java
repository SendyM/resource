
package com.meilitech.zhongyi.resource.dao;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface ResourceStatisticsRepository extends CrudRepository<UrlStatisticsDao, Long> {

    /**
     *
     * @return
     */
    //@Query("SELECT * from resource_task WHERE provider in ('task_center','data_01','inner_spider_news_01')  AND ymd=:ymd limit 1000")
    //List<ResourceTaskDao> getData(@Param("ymd") String ymd);

    @Query("SELECT * from resource_statistics  limit 1000")
    List<ResourceStatisticsDao> getData();




    @Query("update resource_statistics set dayUpdateCount=:dayUpdateCount where ymd=:ymd and provider = :provider")
    void updateCounter( @Param("provider") String provider,@Param("ymd") LocalDate ymd,@Param("dayUpdateCount") int dayUpdateCount);


    @Query("SELECT * from resource_statistics where ymd = :ymd ALLOW FILTERING")
    List<ResourceStatisticsDao> getListByDay(@Param("ymd") String ymd);
}