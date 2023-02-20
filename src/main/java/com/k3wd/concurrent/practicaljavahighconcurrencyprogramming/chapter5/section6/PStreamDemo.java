package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter5.section6;

/**
 * (i+j)*i/2
 * 拆分成3步
 * j = i+j
 * i = j*i
 * i = i/2
 * 并行流水线
 *
 * @author k3wd
 * @date 2023/2/19
 */
public class PStreamDemo {
    public static void main(String[] args) {
        new Thread(new Plus(),"plus线程").start();
        new Thread(new Multiply(),"multiply线程").start();
        new Thread(new Div(),"div线程").start();
        for (int i = 1; i <= 1000; i++) {
            for (int j = i; j <= 1000; j++) {
                Msg msg = new Msg();
                msg.i = i;
                msg.j = j;
                msg.orgStr = "((" + i + "+" + j + ")*" + i + ")/2";
                Plus.bq.add(msg);
            }
        }
    }
}
