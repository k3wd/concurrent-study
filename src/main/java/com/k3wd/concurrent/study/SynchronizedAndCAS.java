package com.k3wd.concurrent.study;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3.Profiler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 研究一下
 *
 * @author k3wd
 * @date 2023/2/14
 */
public class SynchronizedAndCAS {
    static final int MAX_THREAD = 10;
    static final int LOOP_TIMES = 100000000;

    static CountDownLatch latch = new CountDownLatch(MAX_THREAD);

    public static void main(String[] args) throws InterruptedException {
        // 原子类对象count，相当于10个人一起做一件事
//        AtomicCount task = new AtomicCount();

        // 使用锁，最终增长累加，相当于10个人一起做一件事情，但是每一次做只能一个人
        Runnable task = new SynchronizedCount();
        
        // 经过测试，cas较慢
        Profiler.begin();
        for (int i = 0; i < MAX_THREAD; i++) {
            Thread thread = new Thread(task, "\tThread" + i);
            thread.start();
        }
        latch.await();
        System.out.println("Time used: \t" + Profiler.end());
    }

    /**
     * 测试使用原子对象进行 修改。
     */
    public static class AtomicCount implements Runnable {
        AtomicInteger count = new AtomicInteger(0);

        public void increase() {
            for (int j = 0; j < LOOP_TIMES; j++) {
                count.incrementAndGet();
            }
            System.out.println(count);
            System.out.println(Thread.currentThread().getName());
        }

        @Override
        public void run() {
            increase();
            latch.countDown();
        }

    }

    public static class SynchronizedCount implements Runnable {
        Integer count = 0;

        public void increase() {
            // 不要在循环里使用synchronized
            synchronized (this) {
                for (int j = 0; j < LOOP_TIMES; j++) {
                    count++;
                }
            }
            System.out.println(count);
            System.out.println(Thread.currentThread().getName());
        }

        @Override
        public void run() {
            increase();
            latch.countDown();
        }
    }

}
