package com.meilitech.zhongyi.resource.api.enums;


import com.meilitech.zhongyi.resource.util.BeanUtil;

/**
 * API接口全局返回码枚举
 *
 * @author Sendy
 * @since 1.2
 */
public enum ResultType {
    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(-1, "系统繁忙"),

    /**
     * 请求成功
     */
    SUCCESS(0, "请求成功"),


    /**
     * 请求的URI地址不存在
     */
    NOT_FOUND(404, "请求的URI地址不存在"),

    /**
     * 其他错误
     */
    OTHER_ERROR(99999, "其他错误");

    /**
     * 结果码
     */
    Integer code;

    /**
     * 结果描述
     */
    String description;

    /**
     * 返回结果枚举构造方法
     *
     * @param code
     *            结果码
     * @param description
     *            结果描述
     */
    ResultType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 通过code得到返回结果对象
     *
     * @param code
     *            结果码
     * @return 结果枚举对象
     */
    public static ResultType get(String code) {
        BeanUtil.requireNonNull(code, "code is null");
        ResultType[] list = values();
        for (ResultType resultType : list) {
            if (code.equals(resultType.getCode().toString())) {
                return resultType;
            }
        }
        return null;
    }

    /**
     * 获得结果码
     *
     * @return 结果码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获得结果描述
     *
     * @return 结果描述
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ResultType{" + "code=" + code + ", description='" + description + '\'' + '}';
    }
}