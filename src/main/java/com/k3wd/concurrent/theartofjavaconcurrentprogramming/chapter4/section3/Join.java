package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.uitls.SleepUtils;

/**
 * 使用join可以达到等待的效果，类似于排队。
 * join()的内部，
 *  while(isAlive()){
 *      wait(0);//wait(0)表示一直休眠，sleep(0)表示过0毫秒之后继续执行，
 *  }
 * @author k3wd
 * @date 2023/2/11
 */

public class Join {
    public static void main(String[] args) {
        // 主线程是队头
        Thread pre = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new ThreadQueue(pre), String.valueOf(i));
            thread.start();
            pre = thread;
        }
        SleepUtils.second(5);
        System.out.println(Thread.currentThread().getName() + " out of team");
    }

    static class ThreadQueue implements Runnable {
        private Thread pre;

        public ThreadQueue(Thread pre) {
            this.pre = pre;
        }

        @Override
        public void run() {
            try {
                // 入队
                pre.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "done,Out of the team.");
        }
    }
}
