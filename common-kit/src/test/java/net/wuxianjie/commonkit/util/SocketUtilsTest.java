package net.wuxianjie.commonkit.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SocketUtilsTest {

    @Test
    void testSendTcp() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            byte[] dataRequest = toUtf8Bytes("你好 TCP！");
            byte[] dataResponse = toUtf8Bytes("来自 TCP 服务的响应数据");

            new Thread(() -> {
                try (Socket socket = serverSocket.accept()) {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    byte[] buffer = new byte[1024];
                    int lengthRead = inputStream.read(buffer);
                    assertThat(lengthRead).isEqualTo(dataRequest.length);
                    byte[] dataRead = Arrays.copyOfRange(
                        buffer, 0, lengthRead
                    );
                    assertThat(dataRead).containsExactly(dataRequest);

                    outputStream.write(dataResponse);
                    outputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            byte[] actualDataResponse = SocketUtils.sendTcp(
                "localhost", port, dataRequest
            );
            assertThat(actualDataResponse).containsExactly(dataResponse);
        }
    }

    @Test
    void testSendTcpWhenNoResponse() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            byte[] dataRequest = toUtf8Bytes("你好 TCP！");

            new Thread(() -> {
                try (Socket socket = serverSocket.accept()) {
                    InputStream inputStream = socket.getInputStream();

                    byte[] buffer = new byte[1024];
                    int lengthRead = inputStream.read(buffer);
                    assertThat(lengthRead).isEqualTo(dataRequest.length);
                    byte[] dataRead = Arrays.copyOfRange(
                        buffer, 0, lengthRead
                    );
                    assertThat(dataRead).containsExactly(dataRequest);

                    // 不写入响应数据
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            byte[] actualDataResponse = SocketUtils.sendTcp(
                "localhost", port, dataRequest
            );
            assertThat(actualDataResponse).isEmpty();
        }
    }

    @Test
    void testSendUdp() throws IOException {
        try (DatagramSocket datagramSocket = new DatagramSocket(0)) {
            int port = datagramSocket.getLocalPort();
            byte[] dataRequest = toUtf8Bytes("你好 UDP！");
            byte[] dataResponse = toUtf8Bytes("来自 UDP 服务的响应数据");

            new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(
                        buffer, buffer.length
                    );
                    datagramSocket.receive(packet);

                    int lengthReceive = packet.getLength();
                    assertThat(lengthReceive).isEqualTo(dataRequest.length);
                    byte[] dataReceive = Arrays.copyOfRange(
                        packet.getData(), 0, lengthReceive
                    );
                    assertThat(dataReceive).containsExactly(dataRequest);

                    packet.setData(dataResponse);
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            byte[] actualDataResponse = SocketUtils.sendUdp(
                "localhost", port, dataRequest
            );
            assertThat(actualDataResponse).containsExactly(dataResponse);
        }
    }

    @Test
    void testSendUdpWhenNoResponse() throws SocketException {
        try (DatagramSocket datagramSocket = new DatagramSocket(0)) {
            int port = datagramSocket.getLocalPort();
            byte[] dataRequest = toUtf8Bytes("你好 UDP！");

            new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(
                        buffer, buffer.length
                    );
                    datagramSocket.receive(packet);

                    int lengthReceive = packet.getLength();
                    assertThat(lengthReceive).isEqualTo(dataRequest.length);
                    byte[] dataReceive = Arrays.copyOfRange(
                        packet.getData(), 0, lengthReceive
                    );
                    assertThat(dataReceive).containsExactly(dataRequest);

                    // 写入空的响应数据
                    byte[] emptyBytes = new byte[0];
                    packet.setData(emptyBytes);
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            byte[] actualDataResponse = SocketUtils.sendUdp(
                "localhost", port, dataRequest
            );
            assertThat(actualDataResponse).isEmpty();
        }
    }

    private byte[] toUtf8Bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

}
