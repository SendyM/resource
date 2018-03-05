package com.meilitech.zhongyi.resource;

import com.datastax.driver.core.*;
import org.junit.Test;

import java.text.SimpleDateFormat;
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
		cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9042)
				.withCredentials("cassandra", "cassandra").withPoolingOptions(poolingOptions).build();
		// 建立连接
		 session = cluster.connect("zhongyi_db");//连接已存在的键空间
		//session = cluster.connect();


		/*Select select = QueryBuilder.select().all().from("resource");
		select.where(QueryBuilder.eq("domain", "www.sznews.com"));
		select.allowFiltering();
		select.enableTracing();

		ResultSet execute = session.execute(select);*/
		UUID uuid = UUID.randomUUID();

		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");

		String query = "INSERT INTO resource ( resourceid,ymd,resourcetaskid,url,urltype,"+
				                               "domain,title,keywords,description,charset,"+
								               "crawlerTime,publishTime,categoryIds,country,"+
											   "createtime,rank,language,provider,status,updatetime,maxcrawlcount)"+
				       "VALUES("+uuid+",'2018-03-05','1','www.baidu.com',1,'www.baidu.com','test','baidu','biadu','UTF-8',1520240853,1520240853,'2','china',1520240853,99,'chinese','HH',0,1520240853,12);";

		String query1 = "DELETE FROM resource WHERE rank=99 and ymd=1520240853 and resourceTaskId='1'and crawlerTime=1520240853 and createtime = 1520240853 and publishTime=1520240853 and updatetime = 1520240853 and resourceId=934b19e0-f186-49bb-8ea7-385937520d86;";



		ResultSet execute = session.execute(query);
		System.out.println(System.currentTimeMillis());

	}
}