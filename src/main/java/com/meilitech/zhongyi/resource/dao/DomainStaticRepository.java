
package com.meilitech.zhongyi.resource.dao;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DomainStaticRepository extends CrudRepository<DomainStaticDao, Long> {

    /**
     *
     * @return
     */
    @Query("SELECT * from domain limit :limit ALLOW FILTERING")
    List<DomainStaticDao> getData(@Param("limit") int limit);

    @Query("SELECT count(*) as count from domain")
    List<DomainStaticDao> getSum();

    @Query("SELECT ymd,count(1) as count from domain where ymd = :ymd")
    List<DomainStaticDao> getListByDay(@Param("ymd") String ymd);

    @Query("SELECT * from domain where ymd = :ymd")
    List<DomainStaticDao> getDetailByDay(@Param("ymd") String ymd);

}