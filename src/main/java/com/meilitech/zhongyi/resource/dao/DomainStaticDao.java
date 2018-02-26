
package com.meilitech.zhongyi.resource.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "domain")
public class DomainStaticDao {

    @PrimaryKey("ymd")
    private String ymd;

    @Column("domain")
    private String domain;

    //@Column("count")
    private String count;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public String getYmd() {
        return ymd;
    }

    public void setYmd(String ymd) {
        this.ymd = ymd;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}