package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter5.section8;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author k3wd
 * @date 2023/2/20
 */
public class PSort {
    static boolean exchange = false;
    static int[] arr;
    static ExecutorService pool = Executors.newCachedThreadPool();


    static synchronized void setIsExchange(boolean isExchange) {
        exchange = isExchange;
    }

    static synchronized boolean isExchange() {
        return exchange;
    }

    public static class OddEventSortTask implements Runnable {
        int i;
        CountDownLatch latch;

        public OddEventSortTask(int i, CountDownLatch latch) {
            this.i = i;
            this.latch = latch;
        }

        @Override
        public void run() {
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
                setIsExchange(true);
            }
            latch.countDown();
        }
    }

    public static void pOddEventSort(int[] arr) throws InterruptedException {
        int start = 0;
        PSort.arr = arr;
        while (isExchange() || start == 0) {
            setIsExchange(false);
            CountDownLatch latch = new CountDownLatch(arr.length / 2 - (arr.length % 2 == 0 ? start : 0));
            for (int i = start; i < arr.length - 1; i = i + 2) {
                new Thread(new OddEventSortTask(i, latch)).start();
            }
            // 等待所有线程结束
            latch.await();
            if (start == 0) {
                start = 1;
            } else {
                start = 0;
            }
        }
    }
}
