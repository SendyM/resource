package com.meilitech.zhongyi.resource.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToolsUtil {
    public static String getTaskId(){
        return new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
    }
}
