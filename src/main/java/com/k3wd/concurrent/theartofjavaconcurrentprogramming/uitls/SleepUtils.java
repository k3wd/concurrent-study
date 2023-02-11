package com.k3wd.concurrent.theartofjavaconcurrentprogramming.uitls;

import java.util.concurrent.TimeUnit;

/**
 * @author k3wd
 * @date 2023/2/11
 */
public class SleepUtils {
    private SleepUtils() {
    }

    ;

    public static final void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
        }
    }
}
