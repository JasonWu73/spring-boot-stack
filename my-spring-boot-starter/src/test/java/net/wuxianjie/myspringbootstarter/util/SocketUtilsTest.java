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
            byte[] dataRequest = StrUtils.toUtf8Bytes("你好 TCP！");
            byte[] dataResponse = StrUtils.toUtf8Bytes("来自 TCP 服务的响应数据");

            // 启动一个线程模拟 TCP 服务端
            new Thread(() -> {
                try (Socket socket = serverSocket.accept()) {
                    InputStream input = socket.getInputStream();
                    OutputStream output = socket.getOutputStream();

                    // 读取并判断请求数据是否正确
                    byte[] buffer = new byte[1024];
                    int lengthRead = input.read(buffer);
                    byte[] dataRead = Arrays.copyOfRange(buffer, 0, lengthRead);
                    Assertions.assertAll(
                        () -> Assertions.assertEquals(dataRequest.length, lengthRead),
                        () -> Assertions.assertArrayEquals(dataRequest, dataRead)
                    );

                    // 写入响应数据
                    output.write(dataResponse);
                    output.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();

            byte[] actualDataResponse = SocketUtils.sendTcp(
                "127.0.0.1", port, dataRequest, 1000, 1000
            );
            Assertions.assertArrayEquals(dataResponse, actualDataResponse);
        }
    }

    @Test
    void sendUdp() throws SocketException {
        // 启动 UDP 服务端进行测试
        try (DatagramSocket socket = new DatagramSocket(0)) {
            int port = socket.getLocalPort();
            byte[] dataRequest = StrUtils.toUtf8Bytes("你好 UDP！");
            byte[] dataResponse = StrUtils.toUtf8Bytes("来自 UDP 服务的响应数据");

            // 启动一个线程模拟 UDP 服务端
            new Thread(() -> {
                try {
                    // 读取并判断请求数据是否正确
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    int lengthReceive = packet.getLength();
                    byte[] dataReceive = Arrays.copyOfRange(
                        packet.getData(), 0, lengthReceive
                    );
                    Assertions.assertAll(
                        () -> Assertions.assertEquals(dataRequest.length, lengthReceive),
                        () -> Assertions.assertArrayEquals(dataRequest, dataReceive)
                    );

                    // 写入空的响应数据
                    packet.setData(dataResponse);
                    socket.send(packet);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();

            byte[] actualDataResponse = SocketUtils.sendUdp(
                "127.0.0.1", port, dataRequest, 1000, 1
            );
            Assertions.assertArrayEquals(dataResponse, actualDataResponse);
        }
    }
}
