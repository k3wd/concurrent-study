package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter5.section6;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author k3wd
 * @date 2023/2/19
 */
public class Div implements Runnable {
    public static BlockingDeque<Msg> bq = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            try {
                Msg msg = bq.take();
                msg.i = msg.i / 2;
                System.out.println(msg.orgStr + "=" + msg.i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
