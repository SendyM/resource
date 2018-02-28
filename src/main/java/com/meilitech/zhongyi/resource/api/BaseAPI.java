package com.meilitech.zhongyi.resource.api;


import com.meilitech.zhongyi.resource.api.enums.ResultType;
import com.meilitech.zhongyi.resource.api.response.BaseResponse;
import com.meilitech.zhongyi.resource.util.BeanUtil;
import com.meilitech.zhongyi.resource.util.CollectionUtil;
import com.meilitech.zhongyi.resource.util.HttpUtil;
import com.meilitech.zhongyi.resource.util.NetWorkCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


/**
 * API基类，提供一些通用方法
 * 通用get post请求等
 *
 * @author Sendy
 * @since 1.2
 */
public abstract class BaseAPI {

    /**
     * 通用post请求
     *
     * @param url  地址，其中token用#代替
     * @param parameters 参数，Map格式
     * @return 请求结果
     */
    protected String sendPost(String url,Map<String, Object> parameters) {
        BeanUtil.requireNonNull(url, "url is null");
        return HttpUtil.sendPost(url, parameters);
    }

    /**
     * 通用post请求
     *
     * @param url  地址，其中token用#代替
     * @param parameters 参数，Map格式
     * @return 请求结果
     */
    protected String sendGet(String url,Map<String, Object> parameters) {
        BeanUtil.requireNonNull(url, "url is null");
        return HttpUtil.sendGet(url, parameters);
    }
    /**
     * 通用post请求
     *
     * @param url  地址，其中token用#代替
     * @param json 参数，json格式
     * @return 请求结果
     */
    protected BaseResponse executePost(String url, String json) {
        return executePost(url, json, null);
    }

    /**
     * 通用post请求
     *
     * @param url  地址
     * @param json 参数，json格式
     * @param file 上传的文件
     * @return 请求结果
     */
    protected BaseResponse executePost(String url, String json, File file) {
        BaseResponse response;
        BeanUtil.requireNonNull(url, "url is null");
        ArrayList files = null;
        if (null != file) {
            files = CollectionUtil.newArrayList(file);
        }
        response = NetWorkCenter.post(url, json, files);
        return response;
    }


    /**
     * 通用get请求
     *
     * @param url 地址
     * @return 请求结果
     */
    protected BaseResponse executeGet(String url) {
        BaseResponse response;
        BeanUtil.requireNonNull(url, "url is null");
        response = NetWorkCenter.get(url);
        return response;
    }

    /**
     * 判断本次请求是否成功
     *
     * @param errCode 错误码
     * @return 是否成功
     */
    protected boolean isSuccess(String errCode) {
        return ResultType.SUCCESS.getCode().toString().equals(errCode);
    }

}
