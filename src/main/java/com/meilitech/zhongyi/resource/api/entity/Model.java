package com.meilitech.zhongyi.resource.api.entity;

import java.io.Serializable;

/**
 * 模型接口，所有需要传输的对象都需要实现，提供转json的方法
 *
 * @author peiyu
 */
public interface Model extends Serializable {
    long serialVersionUID = 1L;

    /**
     * 将model转成json字符串
     *
     * @return json字符串
     */
    String toJsonString();
}
