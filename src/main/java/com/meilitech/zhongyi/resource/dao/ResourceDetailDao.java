
package com.meilitech.zhongyi.resource.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "resource_detail")
public class ResourceDetailDao {

    @PrimaryKey("provider")
    private String provider;

    @Column("ymd")
    private LocalDate ymd;

    @Column("domain")
    private String domain;

    @Column("resourceTaskId")
    private String resourceTaskId;

    @Column("url")
    private String url;

    @Column("referer")
    private String referer;

    @Column("createTime")
    private Instant createTime;

    @Column("crawlerStatus")
    private int crawlerStatus;


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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getResourceTaskId() {
        return resourceTaskId;
    }

    public void setResourceTaskId(String resourceTaskId) {
        this.resourceTaskId = resourceTaskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant  createTime) {
        this.createTime = createTime;
    }

    public int getCrawlerStatus() {
        return crawlerStatus;
    }

    public void setCrawlerStatus(int crawlerStatus) {
        this.crawlerStatus = crawlerStatus;
    }
}