create keyspace zhongyi_db WITH REPLICATION = {'class' : 'SimpleStrategy','replication_factor': 2};

/*存放域名以及列表页*/
CREATE TABLE resource (
  resourceId UUID,
  ymd date,
  resourceTaskId VARCHAR,
  url varchar,
  urlType tinyint,
  domain varchar,
  title varchar,
  keywords varchar,
  description varchar,
  charset varchar,
  crawlerTime timestamp,/*最后一次抓取时间*/
  publishTime timestamp,
  categoryIds varchar,
  country varchar,
  createTime timestamp,
  rank tinyint,
  language varchar,
  provider varchar,
  status tinyint,
  updateTime timestamp,
  maxCrawlCount int,
  PRIMARY KEY (rank,resourceTaskId,ymd,crawlerTime,createTime,publishTime,updateTime,resourceId))
  WITH CLUSTERING ORDER BY (resourceTaskId desc,ymd desc,crawlerTime desc,createTime desc,publishTime desc,updateTime desc,resourceId desc)
  AND GC_GRACE_SECONDS = 0;


  /*DROP INDEX IF EXISTS zhongyi_db.id_resourceTaskId;*/
  CREATE INDEX  IF NOT EXISTS idx_resourceTaskId ON zhongyi_db.resource (resourceTaskId);
  /*CREATE INDEX  IF NOT EXISTS idx_domain ON zhongyi_db.resource (domain);*/
  CREATE INDEX  IF NOT EXISTS idx_ymd ON zhongyi_db.resource (ymd);


  /*DROP INDEX IF EXISTS zhongyi_db.idx_domain;*/
  CREATE CUSTOM INDEX idx_domain ON zhongyi_db.resource (domain) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer', 'case_sensitive': 'false'};

  /*存放url明细*/
  CREATE TABLE resource_detail (
    provider VARCHAR,
    ymd date,
    domain varchar,
    resourceTaskId VARCHAR,
    url varchar,
    referer varchar,
    createTime timestamp,
    crawlerStatus int,
    PRIMARY KEY ((provider,ymd),domain,resourceTaskId,url)
  )WITH CLUSTERING ORDER BY (domain desc,resourceTaskId desc,url desc)
  AND GC_GRACE_SECONDS = 0;

 /*每日更新URL统计*/
  CREATE TABLE url_statistics (
    provider VARCHAR,
    ymd date,
    domain varchar,
    dayUpdateCount counter,
    PRIMARY KEY ((provider,ymd),domain)
  )WITH CLUSTERING ORDER BY (domain desc)
  AND GC_GRACE_SECONDS = 0;


 /*每日数据源更新统计*/
  CREATE TABLE resource_statistics (
    provider VARCHAR,
    ymd date,
    dayUpdateCount int,
    PRIMARY KEY ((provider,ymd))
  )WITH GC_GRACE_SECONDS = 0;

/*废弃*/
  CREATE TABLE domain (
    ymd VARCHAR ,
    domain varchar,
    PRIMARY KEY (ymd,domain)
  )WITH  GC_GRACE_SECONDS = 0;


/*废弃*/
  CREATE TABLE zhongyi_db.domain_source (
    id uuid,
    domain text,
    status tinyint,
    PRIMARY KEY (id, domain)
) WITH CLUSTERING ORDER BY (domain ASC)


/*清空数据脚本*/
truncate resource;
truncate resource_detail;
truncate url_statistics;
truncate resource_statistics;

