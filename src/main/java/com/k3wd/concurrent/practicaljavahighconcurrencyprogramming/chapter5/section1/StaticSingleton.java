package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter5.section1;

/**
 * @author k3wd
 * @date 2023/2/18
 */
public class StaticSingleton {
    private StaticSingleton() {
        System.out.println("StaticSingleton is create");
    }
    

    private static class SingletonHolder {
        private static StaticSingleton instance = new StaticSingleton();
    }

    public static StaticSingleton getInstance() {
        return SingletonHolder.instance;
    }
}
