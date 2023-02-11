package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * 主线程注入通过PipedWriter写入，在printTread通过PipedReader将内容紧随其后读出并打印。
 * @author k3wd
 * @date 2023/2/11
 */
public class Piped {
    public static void main(String[] args) throws IOException {
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        // 将输出流和输入流进行连接
        out.connect(in);
        Thread printThread = new Thread(new Print(in), "PrintThread");
        printThread.start();
        int receive = 0;
        try {
            while ((receive = System.in.read()) != -1) {
                out.write(receive);
            }
        } finally {
            out.close();
        }
    }

    static class Print implements Runnable {
        private PipedReader in;

        public Print(PipedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            int receive = 0;
            try {
                while ((receive = in.read()) != -1) {
                    System.out.println((char) receive);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
