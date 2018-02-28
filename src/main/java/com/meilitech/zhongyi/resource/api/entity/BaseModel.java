package com.meilitech.zhongyi.resource.api.entity;


import com.meilitech.zhongyi.resource.util.JSONUtil;

/**
 * 抽象实体类
 *
 * @author Sendy
 */
public abstract class BaseModel implements Model {

    @Override
    public String toJsonString() {
        return JSONUtil.toJson(this);
    }

}

