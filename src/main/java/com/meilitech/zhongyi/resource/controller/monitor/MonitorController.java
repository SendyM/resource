package com.meilitech.zhongyi.resource.controller.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilitech.zhongyi.resource.api.BaseAPI;
import com.meilitech.zhongyi.resource.api.response.BaseResponse;
import com.meilitech.zhongyi.resource.controller.ResourceController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "服务状态监控")
@RequestMapping("/passive")
@EnableAutoConfiguration
@RestController
public class MonitorController extends BaseAPI {
    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Autowired
    Environment env;

    /**
     * 被动心跳
     *
     * @param providerCode  资源编码
     * @param operationStatus 运行状态
     * @return
     */
    @ApiOperation(value="被动心跳服务", notes="被动心跳服务", response = BaseResponse.class)
    @RequestMapping(method = RequestMethod.POST, value = "/heart")
    public String  heart(
            @ApiParam(value = "资源编码,如果有多个的话用逗号隔开",required = true,defaultValue = "zyzy_39")
            @RequestParam(value = "providerCode") String  providerCode,
            @ApiParam(value = "运行状态(1:运行中0:已停用)",required = true)
            @RequestParam(value = "operationStatus") String operationStatus) {

            Map<String, Object> map = new HashMap<>( 2 );
            map.put( "providerCode",providerCode );
            map.put( "operationStatus",operationStatus );
            Map<String, Object> reqMap = new HashMap<>( 1 );
            reqMap.put( "data", map);

            String url = env.getProperty( "resource.heart.url" );

        String msg = sendPost( url, reqMap );
        System.out.println( msg );

        return msg;
    }

}
