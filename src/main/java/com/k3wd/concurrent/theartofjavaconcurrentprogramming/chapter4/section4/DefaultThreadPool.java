package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section4;


import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author k3wd
 * @date 2023/2/12
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    /**
     * 线程池最大限制数
     */
    private final int MAX_WORKER_NUMBERS = 10;
    /**
     * 线程池最小数量
     */
    private final int MIN_WORKER_NUMBERS = 1;

    /**
     * 线程池默认线程数
     */
    private static final int DEFAULT_WORKER_NUMBERS = 5;

    /**
     * 工人名单
     */
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<>());

    /**
     * 任务清单
     */
    private final LinkedList<Job> jobs = new LinkedList<Job>();

    /**
     * 线程编号生成
     */
    private AtomicLong threadNum = new AtomicLong();

    private int workerNum = DEFAULT_WORKER_NUMBERS;


    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    /**
     * @param num 指定工作线程数
     */
    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }


    /**
     * 分配工人自己领取任务
     *
     * @param num
     */
    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum);
            threadNum.incrementAndGet();
            thread.start();
        }
    }

    /**
     * 工人要从任务清单中领取任务执行
     */
    class Worker implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    while (jobs.isEmpty()) {
                        try {
                            // 没任务，等着干活
                            jobs.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                    if (job != null) {
                        try {
                            job.run();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }

    /**
     * 接到一个任务
     *
     * @param job 执行一个Job，这个Job需要实现Runnable
     */
    @Override
    public void execute(Job job) {
        if (job != null) {
            synchronized (jobs) {
                jobs.addLast(job);
                // 任务加进来了，通知工人干活
                jobs.notify();
            }
        }
    }

    /**
     * 解散项目组
     */
    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            if (workers.remove(worker)) {
                worker.shutdown();
            }
        }
    }

    /**
     * 招新
     */
    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            // 限制新增的Worker数量不超过最大值
            if (num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    /**
     * 裁员
     */
    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num >= this.workerNum) {
                throw new IllegalArgumentException("beyond workerNum");
            }
            // 按照给定的数量停止work
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(workers)) {
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }
}
