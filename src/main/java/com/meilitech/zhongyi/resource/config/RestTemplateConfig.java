package com.meilitech.zhongyi.resource.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author  Sendy
 */
@Configuration
public class RestTemplateConfig {
	@Bean
	public RestOperations restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
				= new HttpComponentsClientHttpRequestFactory(closeableHttpClient);

//		clientHttpRequestFactory.setConnectionRequestTimeout(3000);
//		clientHttpRequestFactory.setConnectTimeout(3000);
//		clientHttpRequestFactory.setReadTimeout(3000);
		//设置超时时间为2秒
//		clientHttpRequestFactory.setConnectionRequestTimeout(2000);
//		clientHttpRequestFactory.setConnectTimeout(2000);
//		clientHttpRequestFactory.setReadTimeout(2000);

//		clientHttpRequestFactory.setConnectionRequestTimeout(1000);
//		clientHttpRequestFactory.setConnectTimeout(1000);
//		clientHttpRequestFactory.setReadTimeout(1000);

		restTemplate.setRequestFactory(clientHttpRequestFactory);

		return restTemplate;
	}
}
