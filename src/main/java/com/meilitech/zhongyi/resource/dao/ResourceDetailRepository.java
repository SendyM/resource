
package com.meilitech.zhongyi.resource.dao;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ResourceDetailRepository extends CrudRepository<ResourceDetailDao, Long> {

    /**
     *
     * @return
     */
    @Query("SELECT * from domain limit :limit ALLOW FILTERING")
    List<ResourceDetailDao> getData(@Param("limit") int limit);

    @Query("SELECT count(*) as count from domain")
    List<ResourceDetailDao> getSum();

    @Query("SELECT ymd,count(1) as count from domain where ymd = :ymd")
    List<ResourceDetailDao> getListByDay(@Param("ymd") String ymd);

    @Query("SELECT * from domain where ymd = :ymd")
    List<ResourceDetailDao> getDetailByDay(@Param("ymd") String ymd);

}