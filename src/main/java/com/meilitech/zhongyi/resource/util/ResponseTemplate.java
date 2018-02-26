package com.meilitech.zhongyi.resource.util;

import com.meilitech.zhongyi.resource.constants.SysError;

import java.util.HashMap;
import java.util.Map;


public class ResponseTemplate extends HashMap<String, Object> {


    public ResponseTemplate() {
        Map<String, Object> body = new HashMap<String, Object>();

        Map<String, Object> request = new HashMap<String, Object>();
        body.put("response", request);

        Map<String, String> result = new HashMap<String, String>();
        result.put("code", "ok");
        result.put("desc", "");
        body.put("result", result);

        this.put("body", body);
    }

    public void setResultCode(String resultCode) {
        ((HashMap<String, String>) ((HashMap<String, Object>) this.get("body")).get("result")).put("code", resultCode);
    }

    public void setResultCode(SysError sysError) {
        ((HashMap<String, String>) ((HashMap<String, Object>) this.get("body")).get("result")).put("code", sysError.name());
        this.setResultMsg(sysError.getMsg());
    }

    public void setResultCode(int resultCode) {
        ((HashMap<String, String>) ((HashMap<String, Object>) this.get("body")).get("result")).put("code", Integer.toString(resultCode));
    }

    public String getResultCode() {
        return ((HashMap<String, String>) ((HashMap<String, Object>) this.get("body")).get("result")).get("code");
    }

    public String getResultMsg() {
        return ((HashMap<String, String>) ((HashMap<String, Object>) this.get("body")).get("result")).get("desc");
    }

    public void setResultMsg(String resultMsg) {
        ((HashMap<String, String>) ((HashMap<String, Object>) this.get("body")).get("result")).put("desc", resultMsg);
    }

    public void setResponse(Object request) {
        ((HashMap<String, Object>) this.get("body")).put("response", request);
    }

    public void setResponse(String key, Object value) {
        ((HashMap<String, Object>) ((HashMap<String, Object>) this.get("body")).get("response")).put(key, value);
    }


}
