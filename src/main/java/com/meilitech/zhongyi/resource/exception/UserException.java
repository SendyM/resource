package com.meilitech.zhongyi.resource.exception;


import com.meilitech.zhongyi.resource.constants.SysError;

public class UserException extends RuntimeException {
    private String errCode;

    public UserException(String errCode) {
        super(SysError.getMsgByError(errCode));
        this.errCode = errCode;
    }

    public UserException(String errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }

    public UserException(SysError sysError) {
        super(sysError.getMsg());
        this.errCode = sysError.name();
    }

    public UserException(SysError sysError, String msg) {
        super(msg);
        this.errCode = sysError.name();
    }


}
