
package com.meilitech.zhongyi.resource.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "resource")
public class ResourceDao {

    public enum UrlType {
        DOMAIN(1), LIST(3);
        private int urlType;

        UrlType(int type) {
            this.urlType = type;
        }

        public int getUrlType() {
            return this.urlType;
        }
    }

    public enum Provider {
        NEWSPRIDER("inner_spider_news_01"),
        TASKCENTER("task_center");
        // 定义私有变量
        private String nCode;

        // 构造函数，枚举类型只能为私有
         Provider(String _nCode) {

            this.nCode = _nCode;

        }

        @Override
        public String toString() {
            return this.nCode;
        }
    }

    @PrimaryKey("resourceId")
    private UUID resourceId;
    @Column("resourceTaskId")
    private String resourceTaskId;
    @Column("ymd")
    private LocalDate ymd;
    @Column("domain")
    private String domain;
    @Column("provider")
    private String provider;
    @Column("url")
    private String url;
    @Column("urlType")
    private int urlType;
    @Column("title")
    private String title;
    @Column("keywords")
    private String keywords;
    @Column("description")
    private String description;
    @Column("charset")
    private String charset;
    @Column("crawlerTime")
    private Date crawlerTime;
    @Column("publishTime")
    private Date publishTime;
    @Column("categoryIds")
    private String categoryIds;
    @Column("country")
    private String country;
    @Column("createTime")
    private Instant createTime;
    @Column("rank")
    private int rank;
    @Column("language")
    private String language;
    @Column("status")
    private int status;
    @Column("updateTime")
    private Date updateTime;
    @Column("maxCrawlCount")
    private String maxCrawlCount;

    private Long dayUpdateCount;

    public String  getMaxCrawlCount(){
        if(this.maxCrawlCount==null){
            return "";
        }
        return maxCrawlCount;
    }


    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Date getCrawlerTime() {
        return crawlerTime;
    }

    public void setCrawlerTime(Date crawlerTime) {
        this.crawlerTime = crawlerTime;
    }


    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

    public String getResourceTaskId() {
        return resourceTaskId;
    }

    public void setResourceTaskId(String resourceTaskId) {
        this.resourceTaskId = resourceTaskId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDate getYmd() {
        return ymd;
    }

    public void setYmd(LocalDate ymd) {
        this.ymd = ymd;
    }
}