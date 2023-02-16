package com.k3wd.concurrent.study;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3.Profiler;
import com.k3wd.concurrent.utils.JedisDBPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 测试一下如何快速的查redis
 *
 * @author k3wd
 * @date 2023/2/14
 */
public class ParallelStream4Redis {
    private static Jedis jedis = JedisDBPool.getConnectJedis();

    public static void main(String[] args) {
        // 需要查的是hashKeyList
        // 查一万次
        List<String> keys = getHashKeyList();
//        Profiler.begin();
//        Map<String, String> result = new HashMap<>();
//        keys.stream().forEach(e -> {
//            if (jedis.exists(e)) {
//                jedis.mget(e);
//            }
//        });
//        System.out.println("耗时：" + Profiler.end() + " ms");

        // 一次查10000条
        Profiler.begin();
        Pipeline pipeline = jedis.pipelined();
        // 获取每个key对应的数据
        for (String key : keys) {
            pipeline.hgetAll(key);
        }
        // 获取结果 
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        // 根据传入key的顺序获取结果。没有就为null
        List<Object> results = pipeline.syncAndReturnAll();
        for (int i = 0; i < results.size(); i++) {
            resultMap.put(keys.get(i), (Map<String, String>) results.get(i));
        }
        
        System.out.println("耗时：" + Profiler.end() + " ms");
    }

    public static List<String> getHashKeyList() {
        final int KEY_NUMBER = 10000;
        List<String> hashKeyList = new ArrayList<>();
        for (int i = 0; i < KEY_NUMBER; i++) {
            String key = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
            hashKeyList.add(key);
        }
        hashKeyList.add("20715a8a31be800");
        return hashKeyList;
    }
}
