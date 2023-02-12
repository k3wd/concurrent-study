package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section4;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * 一个简单的数据库连接池，在实例中模拟从连接池中获取、使用和释放连接的过程，而客户端获取连接的过程被设定为等待超时的模式。
 * 也就是在1000毫秒内如果无法获取到可用连接，将会返回给客户端一个null。设定连接池的大小为10个，然后通过调节客户端的线程数来模拟无法获取连接的场景。
 * 连接池的定义：
 * 它通过构造函数初始化连接的最大上限，通过一个双向队列来维护连接，调用方需要先调用fetchConnection(long)方法来指定在多少毫秒内超时获取连接，
 * 当连接使用完成后，需要调用releaseConnection(Connection)方法将连接放回线程池。
 * 保证等待不到资源的线程“按时返回”。
 *
 * @author k3wd
 * @date 2023/2/11
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        if (initialSize > 0) {
            //获取连接
            for (int i = 0; i < initialSize; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 完全超时
            if (mills <= 0) {
                while (pool.isEmpty()) {
                    wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                // 在指定时间内尝试获取连接
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    // 有空闲连接，被提前唤醒；
                    // 无空闲连接，到期；
                    // 无空闲连接，但被唤醒；
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                // 判断是否是pool中有连接了
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                // 连接释放后需要进行通知，这样其他消费者就能感知到连接池中已经归还了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }
}
