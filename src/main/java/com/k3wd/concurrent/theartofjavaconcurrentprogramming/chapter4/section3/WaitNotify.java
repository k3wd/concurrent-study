package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.uitls.SleepUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 4.3.3
 * 等待通知经典范式
 * 等待方：
 * 1）获取对象的锁
 * 2）条件不满足，那么调用对象的wait方法，被通知后仍要检查条件。
 * 3）条件满足则执行对应的逻辑
 * <p>
 * 通知方：
 * 1）获取对象的锁
 * 2）改变条件
 * 3）通知所有等待在对象的线程
 *
 * @author k3wd
 * @date 2023/2/11
 */
public class WaitNotify {
    static boolean flag = true;
    static final Object LOCK = new Object();

    public static void main(String[] args)  {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        SleepUtils.second(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {

        @Override
        public void run() {
            synchronized (LOCK) {
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + "flag is true. wait [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
                        // 释放了锁
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(Thread.currentThread() + "flag is false. [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
        }
    }

    static class Notify implements Runnable {

        @Override
        public void run() {
            synchronized (LOCK) {
                System.out.println(Thread.currentThread() + "持有了锁 [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
                // 通知
                LOCK.notifyAll();
                flag = false;
                //并没有释放锁
                SleepUtils.second(5);
            }
            synchronized (LOCK) {
                System.out.println(Thread.currentThread() + "再次持有了锁.  [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
                SleepUtils.second(5);
            }
        }
    }
}
