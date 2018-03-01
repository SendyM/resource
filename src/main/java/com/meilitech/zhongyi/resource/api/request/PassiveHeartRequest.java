package com.meilitech.zhongyi.resource.api.request;

import com.meilitech.zhongyi.resource.api.entity.PassiveHeart;
import lombok.Data;

/**
 * 被动心跳请求
 */
@Data
public class PassiveHeartRequest {

    /**
     * 被动心跳请求参数
     */
    private PassiveHeart data;
}
