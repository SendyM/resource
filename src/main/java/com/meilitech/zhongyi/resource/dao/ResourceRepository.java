
package com.meilitech.zhongyi.resource.dao;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface ResourceRepository extends CrudRepository<ResourceDao, Long> {

    /**
     * @return
     */
    //@Query("SELECT resouceId,url,urlType,domain,referer,title,keywords,description,charset,crawlerStatus,categoryIds,country,rank,language,provider,referer,status,updateTime from resource limit 10")
    List<ResourceDao> findAll();


    @Query("SELECT * from resource limit :limit ALLOW FILTERING")
    List<ResourceDao> getData(@Param("limit") int limit);


    @Query("SELECT * from resource where ymd = :ymd")
    List<ResourceDao> getDetailByDay(@Param("ymd") String ymd);


    //    select count(1) as sum from resource where ymd='2018-01-01';
    @Query("SELECT count(1)  from resource where ymd = :ymd And provider = :provider  ALLOW FILTERING")
    int countByYmdAndProvider(@Param("ymd") LocalDate ymd,@Param("provider") String provider);

    @Query("SELECT * from resource where domain=:domain  allow filtering")
    List<ResourceDao> getResourceByDomain(@Param("domain") String domain);

    }