package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter4.section4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 让普通的变量也享受CAS操作带来的线程安全性
 *
 * @author k3wd
 * @date 2023/2/18
 */
public class AtomicIntegerFieldUpdaterDemo {
    public static class Candidate {
        int id;
        volatile int score;
    }

    /**
     * 将指定变量包装成线程安全的变量
     *  1、不能设置为private
     *  2、需要带volatile
     *  3、不支持static
     */
    public final static AtomicIntegerFieldUpdater<Candidate> scoreUpdater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    public static volatile int normalScore;

    // 检查Updater 是否正确工作
    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate stu = new Candidate();
        Thread[] t = new Thread[10];
        for (int i = 0; i < 10; i++) {
            t[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    if (Math.random() > 0.4) {
                        normalScore = normalScore +1;
                        scoreUpdater.incrementAndGet(stu);
                        allScore.incrementAndGet();
                    }
                }
            });
            t[i].start();
        }
        for (int i = 0; i < 10; i++) {
            t[i].join();
        }
        System.out.println("normalScore=" + normalScore);
        System.out.println("score=" + stu.score);
        System.out.println("allScore=" + allScore);
    }
}
