package com.meilitech.zhongyi.resource.api.entity;

import lombok.Data;


/**
 * 被动心跳参数
 *
 * @author Sendy
 */
@Data
public class PassiveHeart  {
    /**
     * 资源编码
     */
    String providerCode;
    /**
     * 运行状态(1:运行中0:已停用)
     */
    String operationStatus;
}
