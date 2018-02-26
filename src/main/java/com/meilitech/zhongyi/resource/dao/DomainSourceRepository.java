
package com.meilitech.zhongyi.resource.dao;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface DomainSourceRepository extends CrudRepository<DomainSourceDao, Long> {

    /**
     *
     * @return
     */
    @Query("SELECT * from domain_source  where  status = 0 limit 100 ALLOW FILTERING")
    List<DomainSourceDao> getData(@Param("limit") int limit);


    @Query("update domain_source set status= :status where domain = :domain and  id = :id")
    void updateSomeStatus(@Param("id") UUID id,@Param("domain") String domain, @Param("status") int status);
}