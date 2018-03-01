package com.meilitech.zhongyi.resource.controller.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilitech.zhongyi.resource.api.entity.PassiveHeart;
import com.meilitech.zhongyi.resource.api.enums.ResultType;
import com.meilitech.zhongyi.resource.api.request.PassiveHeartRequest;
import com.meilitech.zhongyi.resource.api.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

@Api(description = "服务状态监控")
@RequestMapping("/passive")
@EnableAutoConfiguration
@RestController
public class MonitorController {
    private static final Logger LOGGER = LoggerFactory.getLogger( MonitorController.class);


    @Autowired
    private RestOperations restTemplate;

    @Value("${resource.heart.url}")
    private String heartUrl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 被动心跳
     *
     * @param providerCode  资源编码
     * @param operationStatus 运行状态
     * @return
     */
    @ApiOperation(value="被动心跳服务", notes="被动心跳服务", response = BaseResponse.class)
    @RequestMapping(method = RequestMethod.POST, value = "/heart")
    public BaseResponse  heart(
            @ApiParam(value = "资源编码,如果有多个的话用逗号隔开",required = true,defaultValue = "zyzy_39")
            @RequestParam(value = "providerCode") String  providerCode,
            @ApiParam(value = "运行状态(1:运行中0:已停用)",required = true)
            @RequestParam(value = "operationStatus") String operationStatus) {

        PassiveHeartRequest passiveHeartRequest = new PassiveHeartRequest();
        PassiveHeart passiveHeart = new PassiveHeart();
        passiveHeart.setOperationStatus(operationStatus);
        passiveHeart.setProviderCode(providerCode);
        passiveHeartRequest.setData(passiveHeart);
        BaseResponse response =new BaseResponse();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON);
            HttpEntity<Object> formEntity = new HttpEntity<>( passiveHeartRequest, headers );
            LOGGER.info("心跳请求数据：{}", objectMapper.writeValueAsString(passiveHeartRequest));
            String msg = restTemplate.postForObject(heartUrl,formEntity, String.class);
            response.setErrmsg( msg );
            response.setErrcode( ResultType.SUCCESS.getCode().toString() );
            LOGGER.info("心跳返回数据：{}", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            LOGGER.info("心跳请求返回结果异常：{}", e);
            return null;
        }
        return response;
    }

}
