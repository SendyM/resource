package com.meilitech.zhongyi.resource.constants;

public enum SysError {
    SUCCESS("ok"),
    PART_SUCCESS(),
    FILE_NOT_EXISTS(),
    FILE_DECOMPRESS_ERR(),
    IMPORT_ERR(),
    IMPORT_PROVIDER_ERR(),
    IMPORT_COL_ERR(),
    IMPORT_CONTENT_EMPTY(),
    IMPORT_COL_LENGTH_ERR("col len err");

    private String msg = "";
    private String name;

    private SysError(String msg) {
        this.msg = msg;
        this.name = this.name();
    }

    private SysError() {
        this.name = this.name();
    }

    public String getMsg() {
        return msg.isEmpty() ? name() : msg;
    }


    public String toString() {
        return this.name;
    }

    public static String getMsgByError(String err) {
        return String.valueOf(SysError.valueOf(err));
    }
}
