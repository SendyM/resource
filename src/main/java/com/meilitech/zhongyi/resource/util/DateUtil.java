package com.meilitech.zhongyi.resource.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Sendy
 */
public class DateUtil {
    public static String getDate(Integer i)  {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -i);
        Date time = c.getTime();
        return format.format(time);
    }
}
