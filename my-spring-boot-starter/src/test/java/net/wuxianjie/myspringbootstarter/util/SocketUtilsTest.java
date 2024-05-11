package net.wuxianjie.myspringbootstarter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SocketUtilsTest {

    @Test
    void sendTcp() throws IOException {
        // 启动 TCP 服务端进行测试
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            byte[] dataReq = StrUtils.toUtf8Bytes("你好 TCP！");
            byte[] dataRes = StrUtils.toUtf8Bytes("来自 TCP 服务的响应数据");

            // 启动一个线程模拟 TCP 服务端
            new Thread(() -> {
                try (Socket socket = serverSocket.accept()) {
                    InputStream input = socket.getInputStream();
                    OutputStream output = socket.getOutputStream();

                    // 读取并判断请求数据是否正确
                    byte[] buf = new byte[1024];
                    int lenRead = input.read(buf);
                    byte[] dataRead = Arrays.copyOfRange(buf, 0, lenRead);
                    Assertions.assertAll(
                        () -> Assertions.assertEquals(dataReq.length, lenRead),
                        () -> Assertions.assertArrayEquals(dataReq, dataRead)
                    );

                    // 写入响应数据
                    output.write(dataRes);
                    output.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();

            byte[] actualDataRes = SocketUtils.sendTcp(
                "127.0.0.1", port, dataReq, 1000, 1000
            );
            Assertions.assertArrayEquals(dataRes, actualDataRes);
        }
    }

    @Test
    void sendUdp() throws SocketException {
        // 启动 UDP 服务端进行测试
        try (DatagramSocket socket = new DatagramSocket(0)) {
            int port = socket.getLocalPort();
            byte[] dataReq = StrUtils.toUtf8Bytes("你好 UDP！");
            byte[] dataRes = StrUtils.toUtf8Bytes("来自 UDP 服务的响应数据");

            // 启动一个线程模拟 UDP 服务端
            new Thread(() -> {
                try {
                    // 读取并判断请求数据是否正确
                    byte[] buf = new byte[1024];
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    socket.receive(pkt);

                    int lenReceive = pkt.getLength();
                    byte[] dataReceive = Arrays.copyOfRange(
                        pkt.getData(), 0, lenReceive
                    );
                    Assertions.assertAll(
                        () -> Assertions.assertEquals(dataReq.length, lenReceive),
                        () -> Assertions.assertArrayEquals(dataReq, dataReceive)
                    );

                    // 写入空的响应数据
                    pkt.setData(dataRes);
                    socket.send(pkt);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();

            byte[] actualDataRes = SocketUtils.sendUdp(
                "127.0.0.1", port, dataReq, 1000, 1
            );
            Assertions.assertArrayEquals(dataRes, actualDataRes);
        }
    }
}
