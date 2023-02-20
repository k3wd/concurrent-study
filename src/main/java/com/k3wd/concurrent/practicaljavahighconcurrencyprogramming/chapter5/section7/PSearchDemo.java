package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter5.section7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 给定数组，分割并行搜索
 *
 * @author k3wd
 * @date 2023/2/19
 */
public class PSearchDemo {
    /**
     * 目标数组
     */
    static int[] arr;
    static Integer THREAD_N = 3;

    /**
     * 保留查询到的结果下标
     */
    static AtomicInteger result = new AtomicInteger(-1);
    static ExecutorService pool = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        initArr();
        pSearch(54);
        System.out.println(result);
    }

    public static class SearchTask implements Callable<Integer> {
        int begin, end, searchValue;

        public SearchTask(int searchValue, int begin, int end) {
            this.begin = begin;
            this.end = end;
            this.searchValue = searchValue;
        }

        @Override
        public Integer call() {
            int re = search(searchValue, begin, end);
            return re;
        }

        public int search(int searchValue, int beginPos, int endPos) {
            System.out.println(Thread.currentThread().getName() + "即将开始寻找" + "从开始位置：" + beginPos + " 到结束位置：" + endPos);
            int i = 0;
            for (i = beginPos; i < endPos; i++) {
                if (result.get() >= 0) {
                    return result.get();
                }
                // 此时尝试存入结果
                if (arr[i] == searchValue) {
                    if (!result.compareAndSet(-1, i)) {
                        return result.get();
                    }
                    return i;
                }
            }
            return -1;
        }
    }

    public static int pSearch(int searchValue) throws InterruptedException, ExecutionException {
        int subAttrSize = arr.length / THREAD_N + 1;
        List<Future<Integer>> re = new ArrayList<>();
        for (int i = 0; i < arr.length; i += subAttrSize) {
            int end = i + subAttrSize;
            if (end >= arr.length) end = arr.length;
            re.add(pool.submit(new SearchTask(searchValue, i, end)));
        }
        for (Future<Integer> fu : re) {
            if (fu.get() >= 0) {
                System.out.println("找到了：" + fu.get());
                return fu.get();
            }
        }
        System.out.println("没找到");
        return -1;
    }

    public static void initArr() {
        arr = new int[100000];
        for (int i = 0; i < 100000; i++) {
            arr[i] = i;
        }
    }
}
