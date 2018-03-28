package com.meilitech.zhongyi.resource.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilitech.zhongyi.resource.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class PassiveHeartTasks {
    private static final Logger log = LoggerFactory.getLogger(PassiveHeartTasks.class);

    @Autowired
    private RestOperations restTemplate;

    @Value("${resource.heart.url}")
    private String heartUrl;


    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 被动心跳
     *
     * 定时发送服务器运行状态
     */
    @Scheduled(fixedDelay = 20000)
    public void  heart() {

        //heartUrl="http://127.0.0.1:8080/manage/api/resource/test1";

        //心跳数据
        String data ="data={\"providerCode\":\"SOURCE_IMPORT_001,SOURCE_SEARCH_001,SOURCE_SEARCH_002,SOURCE_IMPORT_002,SOURCE_SEARCH_003,SOURCE_IMPORT_003\",\"operationStatus\":\"1\"}";

        String returnString = HttpUtil.post(heartUrl,data);

        log.info("心跳返回数据：{}", returnString);

       /* PassiveHeartRequest passiveHeartRequest = new PassiveHeartRequest();
        PassiveHeart passiveHeart = new PassiveHeart();
        passiveHeart.setOperationStatus("1");
        passiveHeart.setProviderCode("SOURCE_IMPORT_001,SOURCE_SEARCH_001,SOURCE_SEARCH_002,SOURCE_IMPORT_002,SOURCE_SEARCH_003,SOURCE_IMPORT_003");
        passiveHeartRequest.setData(passiveHeart);
        BaseResponse response =new BaseResponse();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON);
            HttpEntity<Object> formEntity = new HttpEntity<>( passiveHeartRequest, headers );
            log.info("心跳请求数据：{}", objectMapper.writeValueAsString(passiveHeartRequest));
            String msg = restTemplate.postForObject(heartUrl,formEntity, String.class);
            response.setErrmsg( msg );
            response.setErrcode( ResultType.SUCCESS.getCode().toString() );
            log.info("心跳返回数据：{}", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            log.info("心跳请求返回结果异常：{}", e);
        }*/

    }



}
