package com.meilitech.zhongyi.resource;

import com.datastax.driver.core.*;
import org.junit.Test;

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

		String query = " UPDATE resource SET maxCrawlCount=12  where domain='www.sznews.com' and rank=1 and resourcetaskid='' and ymd='2018-02-28 00:00:00' and crawlertime='2018-02-28 00:00:00'and createtime=2018-02-28 00:00:00 and publishtime=2018-02-28 00:00:00  and updatetime=2018-02-28 00:00:00  and resourceid=''; ";
		ResultSet execute = session.execute(query);
		System.out.println(execute.all());
	}
}
