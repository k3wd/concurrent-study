package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section4;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3.Profiler;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author k3wd
 * @date 2023/2/11
 */
public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);
    /**
     * 保证所有ConnectionRunner能够同时开始
     */
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        Profiler.begin();
        // 线程数量
        int threadCount = 20;
        end = new CountDownLatch(threadCount);
        // 获取次数
        int count = 600;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }
        // 保证上面线程执行完毕
        end.await();
        System.out.println("总共执行:" + (threadCount * count));
        System.out.println("获取连接数:" + got);
        System.out.println("获得失败的连接数" + notGot);
        System.out.println("Cost: " + Profiler.end() + " mills");
    }

    // 获取器
    static class ConnectionRunner implements Runnable {
        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        public void run() {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (count > 0) {
                try {
                    // 从线程池中获取连接，如果1000ms内无法获取到，将会返回null
                    // 分别统计连接获取的数量got和为获取到的数量notGot
                    Connection connection = pool.fetchConnection(200);
                    if (connection != null) {
                        try {
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }
}
