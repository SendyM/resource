package com.meilitech.zhongyi.resource.api.response;


import com.meilitech.zhongyi.resource.api.entity.BaseModel;
import com.meilitech.zhongyi.resource.api.enums.ResultType;
import com.meilitech.zhongyi.resource.util.BeanUtil;
import com.meilitech.zhongyi.resource.util.StrUtil;
import lombok.Data;

/**
 * 钉钉API响应报文对象基类
 *
 * @author mjm
 */
@Data
public class BaseResponse extends BaseModel {

    /**
     * 错误码
     */
    private String errcode;
    /**
     * 错误信息
     */
    private String errmsg;


    public String getErrmsg() {
        String result = this.errmsg;
        //将接口返回的错误信息转换成中文，方便提示用户出错原因
        if (StrUtil.isNotBlank(this.errcode) && !ResultType.SUCCESS.getCode().toString().equals(this.errcode)) {
            ResultType resultType = ResultType.get(this.errcode);
            if(BeanUtil.nonNull(resultType)) {
                result = resultType.getDescription();
            }
        }
        return result;
    }

}
