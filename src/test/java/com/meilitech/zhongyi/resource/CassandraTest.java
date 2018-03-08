package com.meilitech.zhongyi.resource;

import com.datastax.driver.core.*;
import com.meilitech.zhongyi.resource.util.DateUtil;
import org.junit.Test;

import java.util.UUID;

public class CassandraTest {
	public Cluster cluster;

	public Session session;

	@Test
	public void connect()
	{
		PoolingOptions poolingOptions = new PoolingOptions();
		// 每个连接的最大请求数 2.0的驱动好像没有这个方法
		poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 32);
		// 表示和集群里的机器至少有2个连接 最多有4个连接
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 2).setMaxConnectionsPerHost(HostDistance.LOCAL, 4)
				.setCoreConnectionsPerHost(HostDistance.REMOTE, 2).setMaxConnectionsPerHost(HostDistance.REMOTE, 4);

		// addContactPoints:cassandra节点ip withPort:cassandra节点端口 默认9042
		// withCredentials:cassandra用户名密码 如果cassandra.yaml里authenticator：AllowAllAuthenticator 可以不用配置
		cluster = Cluster.builder().addContactPoints("180.76.248.61").withPort(9042)
				.withCredentials("zhongyi", "zhongyi").withPoolingOptions(poolingOptions).build();
		// 建立连接
		 session = cluster.connect("zhongyi_db");//连接已存在的键空间
		//session = cluster.connect();

		/*Select select = QueryBuilder.select().all().from("resource");
		select.where(QueryBuilder.eq("domain", "www.sznews.com"));
		select.allowFiltering();
		select.enableTracing();

		ResultSet execute = session.execute(select);*/
		UUID uuid = UUID.randomUUID();
		String query = "INSERT INTO resource ( resourceid,ymd,resourcetaskid,url,urltype,"+
				                               "domain,title,keywords,description,charset,"+
								               "crawlerTime,publishTime,categoryIds,country,"+
											   "createtime,rank,language,provider,status,updatetime,maxcrawlcount)"+
				       "VALUES("+uuid+",'2018-03-05','1','www.baidu.com',1,'www.baidu.com','test','baidu','biadu','UTF-8',1520240853,1520240853,'2','china',1520240853,99,'chinese','HH',0,1520240853,12);";

		String query1 = "DELETE FROM resource WHERE rank=99 and ymd=1520240853 and resourceTaskId='1'and crawlerTime=1520240853 and createtime = 1520240853 and publishTime=1520240853 and updatetime = 1520240853 and resourceId=934b19e0-f186-49bb-8ea7-385937520d86;";

		String query2 = "INSERT INTO url_statistics ( provider,ymd,domain,dayUpdateCount)"+
				         "VALUES('inner_spider_news_01','2018-03-05','zx.jiaju.sina.com.cn',12);";
		String query3= "UPDATE url_statistics SET dayUpdateCount=dayUpdateCount+5  WHERE provider='inner_spider_news_01'and ymd='2018-03-05'and domain='zx.jiaju.sina.com.cn';";

		String query4= "SELECT  dayUpdateCount FROM  url_statistics  WHERE provider='HH'and ymd<='2018-03-07' and ymd>='2018-03-01' and domain='www.baidu.com'  ALLOW FILTERING ;";

		String query5= "UPDATE resource SET maxcrawlcount=99  WHERE rank=99 and resourceTaskId = '1'and ymd= '2018-03-05' " +
						"and crawlerTime = '1520240853'and createTime = '1520240853' and publishTime = '1520240853'and  updateTime= '1520240853'" +
						"and resourceId = 99afb796-f7ac-4c6c-b5f8-624e5c236bb3;";

		String query6= "SELECT * FROM resource WHERE domain='www.sznews.com' AND provider='task_center' ALLOW FILTERING;";

		String startTime = DateUtil.getDate(30);
		String endTime = DateUtil.getDate(1);

		String query7 = "select domain,dayUpdateCount from  url_statistics  where  ymd>='" + startTime + "' and ymd<='" + endTime + "' and domain='zx.jiaju.sina.com.cn'  allow filtering ";

		String query8 = "select rank from resource where  resourceTaskId = '20180227052444757' and ymd= '2018-02-27' and crawlerTime = '1506614622000' and createTime = '1506614622000' and publishTime = '0' and updateTime= '1519723484757' and resourceId = 264d95f2-aefd-40dc-8e93-f30e4179899d   ALLOW FILTERING;";

		ResultSet resultSet = session.execute(query8);

		Long dayUpdateCount;
		Long sum = 0L;
		for (Row row : resultSet) {
			dayUpdateCount = row.getLong(1);
			sum = sum+dayUpdateCount;
		}
		System.out.println(sum);

	}
}