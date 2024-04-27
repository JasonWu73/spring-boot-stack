package net.wuxianjie.webkit.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

class SocketUtilsTest {

    @Test
    void sendTcp_shouldCorrectlySendAndReceiveData() throws IOException {
        try (var server = new ServerSocket(0)) {
            var port = server.getLocalPort();
            var req = "你好 TCP！".getBytes(StandardCharsets.UTF_8);
            var res = "来自 TCP 服务的响应数据".getBytes(StandardCharsets.UTF_8);

            new Thread(() -> {
                try (var client = server.accept()) {
                    var is = client.getInputStream();
                    var os = client.getOutputStream();

                    var buf = new byte[1024];
                    var len = is.read(buf);
                    Assertions.assertThat(len).isEqualTo(req.length);
                    Assertions.assertThat(Arrays.copyOfRange(buf, 0, len))
                            .containsExactly(req);

                    os.write(res);
                    os.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            var result = SocketUtils.sendTcp("localhost", port, req);
            Assertions.assertThat(result).containsExactly(res);
        }
    }

    @Test
    void sendTcp_shouldCorrectlySendData_whenNoResponse() throws IOException {
        try (var server = new ServerSocket(0)) {
            var port = server.getLocalPort();
            var req = "你好 TCP！".getBytes(StandardCharsets.UTF_8);

            new Thread(() -> {
                try (var client = server.accept()) {
                    var is = client.getInputStream();

                    var buf = new byte[1024];
                    var len = is.read(buf);
                    Assertions.assertThat(len).isEqualTo(req.length);
                    Assertions.assertThat(Arrays.copyOfRange(buf, 0, len))
                            .containsExactly(req);

                    // 不写入响应数据
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            var result = SocketUtils.sendTcp("localhost", port, req);
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Test
    void sendUdp_shouldCorrectlySendAndReceiveData() throws IOException {
        try (var server = new DatagramSocket(0)) {
            var port = server.getLocalPort();
            var req = "你好 UDP！".getBytes(StandardCharsets.UTF_8);
            var res = "来自 UDP 服务的响应数据".getBytes(StandardCharsets.UTF_8);

            new Thread(() -> {
                try {
                    var buf = new byte[1024];
                    var packet = new DatagramPacket(buf, buf.length);
                    server.receive(packet);

                    Assertions.assertThat(packet.getLength()).isEqualTo(req.length);
                    Assertions.assertThat(Arrays.copyOfRange(packet.getData(),
                                    0, packet.getLength()))
                            .containsExactly(req);

                    packet.setData(res);
                    server.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            var result = SocketUtils.sendUdp("localhost", port, req);
            Assertions.assertThat(result).containsExactly(res);
        }
    }

    @Test
    void sendUdp_shouldCorrectlySendData_whenNoResponse() throws SocketException {
        try (var server = new DatagramSocket(0)) {
            var port = server.getLocalPort();
            var req = "你好 UDP！".getBytes(StandardCharsets.UTF_8);

            new Thread(() -> {
                try {
                    var buf = new byte[1024];
                    var packet = new DatagramPacket(buf, buf.length);
                    server.receive(packet);

                    Assertions.assertThat(packet.getLength()).isEqualTo(req.length);
                    Assertions.assertThat(Arrays.copyOfRange(packet.getData(),
                                    0, packet.getLength()))
                            .containsExactly(req);

                    // 不写入响应数据
                    packet.setData(new byte[0]);
                    server.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            var result = SocketUtils.sendUdp("localhost", port, req);
            Assertions.assertThat(result).isEmpty();
        }
    }

}