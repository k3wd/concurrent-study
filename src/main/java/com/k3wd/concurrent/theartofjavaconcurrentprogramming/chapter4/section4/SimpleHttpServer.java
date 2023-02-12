package com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section4;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3.Profiler;
import com.sun.deploy.net.HttpUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 一个基于线程池技术的简单web服务器
 *
 * @author k3wd
 * @date 2023/2/12
 */
public class SimpleHttpServer {
    /**
     * 处理Http的线程池
     */
    static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<>(1);

    static String basePath;
    static ServerSocket serverSocket;
    /**
     * 服务监听端口
     */
    static int port = 8080;

    /**
     * 设置监听端口
     *
     * @param port 端口号
     */
    public static void setPort(int port) {
        if (port > 0) {
            SimpleHttpServer.port = port;
        }
    }

    /**
     * 设置路径
     *
     * @param basePath 路径
     */
    public static void setBasePath(String basePath) {
        if (basePath != null && new File(basePath).exists() && new File(basePath).isDirectory()) {
            SimpleHttpServer.basePath = basePath;
        }
    }

    /**
     * 启动SimpleHttpServer
     *
     * @throws Exception
     */
    public static void start() throws Exception {
        serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        Socket socket = null;
        while ((socket = serverSocket.accept()) != null) {
            // 接收一个客户端Socket，生成一个HttpRequestHandler，放入线程池执行
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }


    static class HttpRequestHandler implements Runnable {
        private final Socket socket;

        public HttpRequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            Profiler.begin();

            String line = null;
            BufferedReader br = null;
            BufferedReader reader = null;
            PrintWriter out = null;
            InputStream in = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = reader.readLine();
                // 由相对路径计算出绝对路径
                String filePath = basePath + header.split(" ")[1];
                filePath = filePath.replace("/", "\\");
                out = new PrintWriter(socket.getOutputStream());
                // 如果请求资源的后最为jpg或者ico，则读取资源并输出
                if (filePath.endsWith("jpg") || filePath.endsWith("ico")) {
                    in = new FileInputStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i = 0;
                    while ((i = in.read()) != -1) {
                        baos.write(i);
                    }
                    byte[] array = baos.toByteArray();
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: image/jpeg");
                    out.println("Content-Length: " + array.length);
                    out.println("");
                    socket.getOutputStream().write(array, 0, array.length);
                } else {
                    br = new BufferedReader(new FileReader(filePath));
                    out = new PrintWriter(socket.getOutputStream());
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Length: text/html;charset=UTF-8");
                    out.println("");
                    while ((line = br.readLine()) != null) {
                        out.println(line);
                    }
                }
                out.println("耗时 ---> " + Profiler.end() + "ms");
                out.flush();
            } catch (Exception e) {
                out.println("HTTP/1.1 500");
                out.println("");
                out.flush();
            } finally {
                close(br, in, reader, out, socket);
            }
        }

        private void close(Closeable... closeables) {
            if (closeables != null) {
                for (Closeable closeable : closeables) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        SimpleHttpServer.setBasePath(System.getProperty("user.dir") + "\\src\\main\\resources");
        try {
            start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
