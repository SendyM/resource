
package com.meilitech.zhongyi.resource.dao;

import com.datastax.driver.core.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "resource_statistics")
public class ResourceStatisticsDao {


    @PrimaryKey("domain")
    private String domain;
    @Column("resourceTaskId")
    private String resourceTaskId;
    @Column("provider")
    private String provider;
    @Column("ymd")
    private LocalDate ymd;
    @Column("dayUpdateCount")
    private Long dayUpdateCount;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public LocalDate getYmd() {
        return ymd;
    }

    public void setYmd(LocalDate ymd) {
        this.ymd = ymd;
    }

    public String getResourceTaskId() {
        return resourceTaskId;
    }

    public void setResourceTaskId(String resourceTaskId) {
        this.resourceTaskId = resourceTaskId;
    }

    public Long getDayUpdateCount() {
        return dayUpdateCount;
    }

    public void setDayUpdateCount(Long dayUpdateCount) {
        this.dayUpdateCount = dayUpdateCount;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}