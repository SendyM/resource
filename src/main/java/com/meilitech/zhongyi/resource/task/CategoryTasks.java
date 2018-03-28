package com.meilitech.zhongyi.resource.task;

import com.meilitech.zhongyi.resource.constants.SysError;
import com.meilitech.zhongyi.resource.dao.ResourceRepository;
import com.meilitech.zhongyi.resource.exception.UserException;
import com.meilitech.zhongyi.resource.service.ResourceService;
import com.meilitech.zhongyi.resource.util.FileUtil;
import com.meilitech.zhongyi.resource.util.RedissonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class CategoryTasks {
    private static final Logger log = LoggerFactory.getLogger(CategoryTasks.class);

    @Autowired
    ResourceRepository resourceRepository;

    RedissonUtil redissonUtil;

    @Autowired
    ResourceService resourceService;

    @Autowired
    Environment env;


    /**
     * 定时读写文件，一次只读一个文件
     */
    @Scheduled(fixedDelay = 5000)
    public void parse() {
        String address = env.getProperty("resource.redis.address");

        redissonUtil = new RedissonUtil(RedissonUtil.BF_RESOURCE, true,address);

        Map<String, Object> map = new HashMap<String, Object>();

        File dir = new File(env.getProperty("resource.data.path") + "xdth_text/");
        File[] filesList = dir.listFiles();
        if (filesList == null) {
            log.info("no file,skip:" + dir.getAbsolutePath());
            return;
        }
        for (File file : filesList) {
            if (!file.isFile()) continue;
            do {
                if (!file.getName().matches(".*\\.gz")) {
                    log.debug("ignore:" + file.getAbsolutePath());
                    continue;
                }

                try {
                    writeDomainContent(file.getAbsolutePath());
                } catch (UserException e) {
                    File newFile = new File(file.getAbsoluteFile() + ".fail");
                    file.renameTo(newFile);
                    log.warn(file.getAbsoluteFile() + ".fail");
                    continue;
                } catch (Exception e) {
                    File newFile = new File(file.getAbsoluteFile() + ".fail");
                    file.renameTo(newFile);
                    log.warn(file.getAbsoluteFile() + ".fail");
                    continue;
                }

                File newFile = new File(file.getAbsoluteFile() + ".done");
                file.renameTo(newFile);
                return;
            } while (false);
        }
    }

    //将filePath文件里面的内容写到对应的域名下
    private void writeDomainContent(String filePath) {

        try {
            File conentFile = new File(filePath);

            if (!FileUtil.decompress(conentFile)) {
                return;
            }

            File file = new File(filePath.replace(".tar.gz", "").replace(".gz", ""));
            // 读取文件，并且以utf-8的形式写出去
            BufferedReader bufread;
            String read;
            bufread = new BufferedReader(new FileReader(file));

            HashMap<String, ArrayList> domainMap = new HashMap<String, ArrayList>();


            //分类文件包含文本
            while ((read = bufread.readLine()) != null) {
                String[] item = read.split("\t");
                if (item.length < 3) {
                    log.warn(String.format("ignore:(%s)", read));
                    continue;
                }

                try {
                    URL url = new URL(item[0]);

                    String domain = url.getHost().toString();
                    if (!domain.matches("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$")) {
                        log.debug("ignore:" + item[0]);
                        continue;
                    }

                    if (!domainMap.containsKey(domain)) {
                        domainMap.put(domain, new ArrayList());
                    }

                    domainMap.get(domain).add(item[2]);
                } catch (MalformedURLException e) {
                    log.warn(String.format("ignore:(%s)", read));
                    continue;
                }

            }

            if (domainMap.size() > 0) {
                File domainDir = new File(env.getProperty("resource.data.path") + "xdth_text_domain/");
                if (!domainDir.exists()) {
                    domainDir.mkdir();
                }

                Iterator iter = domainMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String domain = entry.getKey().toString();
                    ArrayList<String> contentList = ((ArrayList) entry.getValue());
                    String content = String.join("\n", contentList);
                    File domainFile = new File(env.getProperty("resource.data.path") + "xdth_text_domain/" + domain);
                    if (domainFile.exists()) {
                        try {
                            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                            FileWriter writer = new FileWriter(domainFile.getAbsolutePath(), true);
                            writer.write(content);
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        domainFile.createNewFile();
                        try {
                            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                            FileWriter writer = new FileWriter(domainFile.getAbsolutePath(), false);
                            writer.write(content);
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new UserException(SysError.IMPORT_ERR, ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new UserException(SysError.IMPORT_ERR, ex.getMessage());
        }

    }

}
