package com.meilitech.zhongyi.resource.util;

import com.meilitech.zhongyi.resource.task.FtpParseTasks;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RedissonUtil {
    public static String BF_RESOURCE = "BF_RESOURCE";
    static RedissonClient redissonClient = null;
    static Map<String, RBloomFilter<String>> bloomFilterMap = new HashMap<>();
    static final Logger log = LoggerFactory.getLogger(RedissonUtil.class);
    private String bloomFilterName;

    public RedissonUtil(String bloomFilterName, long l, double v, boolean createIfNoExists) {
        this.bloomFilterName = bloomFilterName;
        init(bloomFilterName, l, v, createIfNoExists);
    }

    public RedissonUtil(String bloomFilterName, boolean createIfNoExists) {
        this.bloomFilterName = bloomFilterName;
        init(bloomFilterName, 100_000_000, 0.03, createIfNoExists);
    }

    private void init(String bloomFilterName, long l, double v, boolean createIfNoExists) {

        // connects to 127.0.0.1:6379 by default
        if (redissonClient == null) {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://180.76.248.61:6379");
            redissonClient = Redisson.create(config);
        }


        if (bloomFilterMap.containsKey(bloomFilterName) && bloomFilterMap.get(bloomFilterName) == null) {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
            bloomFilter.tryInit(l, v);
            bloomFilterMap.put(bloomFilterName, bloomFilter);
        }

        if (!bloomFilterMap.containsKey(bloomFilterName) && createIfNoExists) {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
            bloomFilter.tryInit(l, v);
            bloomFilterMap.put(bloomFilterName, bloomFilter);
        }


        if (!bloomFilterMap.get(bloomFilterName).isExists()) {
            //log.error("bloomFilter init error");
        }
    }


    public boolean add(String val) {
        return bloomFilterMap.get(bloomFilterName).add(val);
    }


    public boolean contains(String val) {
        return bloomFilterMap.get(bloomFilterName).contains(val);

    }


}
