package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section4;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.uitls.SleepUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Connection是一个接口，最终的实现由数据库驱动提供方来实现的。
 * 这里使用动态代理构造了一个Connection
 *
 * @author k3wd
 * @date 2023/2/11
 */
public class ConnectionDriver {
    private static final String CLOSE = "close";
    private static final String COMMIT = "commit";

    private
    static class ConnectionHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (COMMIT.equals(method.getName())) {
                SleepUtils.milliSecond(100);
            } else if (CLOSE.equals(method.getName())) {
                System.out.println("========>connection has closed");
            }
            return null;
        }
    }

    /**
     * 创建一个Connection的代理，在commit时休眠100毫秒
     */
    public static final Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class}, new ConnectionHandler());
    }
}
