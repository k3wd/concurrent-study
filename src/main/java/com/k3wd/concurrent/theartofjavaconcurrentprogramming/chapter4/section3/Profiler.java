package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3;

import java.util.concurrent.TimeUnit;

/**
 * 计时器
 * ThreadLocal对象为key，任意对象为值，附带在线程上。
 * 通过set方法设置一个值，当前线程下再get()可以获取到原先设置的值。
 * @author k3wd
 * @date 2023/2/11
 */
public class Profiler {
    
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<>();

    public static final void begin() {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end() {
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) throws Exception {
        Profiler.begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Cost: " + Profiler.end() + " mills");
    }
}
