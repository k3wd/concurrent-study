package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section4;

/**
 * @author k3wd
 * @date 2023/2/12
 */
public interface ThreadPool<Job extends Runnable> {
    /**
     * 执行任务
     *
     * @param job 执行一个Job，这个Job需要实现Runnable
     */
    void execute(Job job);

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 添加工作者线程
     *
     * @param num 数量
     */
    void addWorkers(int num);

    /**
     * 减少工作者线程
     *
     * @param num 数量
     */
    void removeWorker(int num);

    /**
     * 得到正在等待执行的任务数量
     *
     * @return 任务数量
     */
    int getJobSize();
}
