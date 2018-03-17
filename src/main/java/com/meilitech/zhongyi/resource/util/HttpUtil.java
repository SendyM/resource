package com.meilitech.zhongyi.resource.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    public static String post(String strURL, String postData) {
        DataOutputStream out=null;
        BufferedReader reader=null;
        HttpURLConnection connection=null;
        String returnString="";
        try {
            // Post请求的url，与get不同的是不需要带参数
            URL postUrl = new URL(strURL);
            // 打开连接
             connection = (HttpURLConnection) postUrl.openConnection();
            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // 默认是 GET方式
            connection.setRequestMethod("POST");
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            //设置本次连接是否自动重定向
            connection.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            connection.connect();
             out = new DataOutputStream(connection.getOutputStream());

            out.writeBytes(postData);
            //流用完记得关
            out.flush();
            out.close();
            //获取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                returnString=returnString+line;
            }
            reader.close();
            //该干的都干完了,记得把连接断了
            connection.disconnect();
        } catch (Exception e) {
            //e.printStackTrace();
            returnString="error";
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
                if(reader!=null){
                    reader.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }

            }catch (Exception e){
            }

        }
    return returnString;
    }
}
