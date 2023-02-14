package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter5.section1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 灵活的lock锁。
 * lock锁的实现基本都是通过 聚合 了一个同步器的子类来完成线程访问控制的
 * @author k3wd
 * @date 2023/2/13
 */
public class LockUseCase {
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        // 不建议将锁的获取过程放在try中，因为如果在获取锁时发生了异常，异常抛出的同时，也会导致锁无故释放
        lock.lock();
        try{
        }finally {
            lock.unlock();
        }
    }

}
