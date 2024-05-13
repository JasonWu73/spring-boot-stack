package net.wuxianjie.myspringbootstarter.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketUtils {

    /**
     * 默认缓冲区大小，单位：字节。
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 发送 TCP 数据包。
     *
     * @param ip TCP 服务端 IP
     * @param port TCP 服务端端口
     * @param data 要发送的数据
     * @param connectTimeout 连接超时时间（单位：毫秒）
     * @param readTimeout 读取超时时间（单位：毫秒）
     * @return TCP 服务端的响应结果
     */
    public static byte[] sendTcp(
        String ip, int port, byte[] data,
        int connectTimeout, int readTimeout
    ) {
        try (Socket socket = new Socket()) {
            InetSocketAddress endpoint = new InetSocketAddress(ip, port);
            socket.connect(endpoint, connectTimeout);

            OutputStream output = socket.getOutputStream();
            output.write(data);
            output.flush();

            socket.setSoTimeout(readTimeout);
            return readSocket(socket);
        } catch (IOException e) {
            throw new RuntimeException(
                "TCP 通信失败 [ip=%s; port=%s]：%s".formatted(
                    ip, port, e.getMessage()
                )
            );
        }
    }

    /**
     * 发送 UDP 数据包。
     *
     * <p>缓冲区 {@code bufferSize} 的大小直接决定了能读取响应数据的最大长度。</p>
     *
     * @param ip UDP 服务端 IP
     * @param port UDP 服务端端口
     * @param data 要发送的数据
     * @param readTimeout 读取超时时间（单位：毫秒）
     * @param bufferSize 缓冲区大小（单位：字节，只有设置大于 {@link #BUFFER_SIZE} 才会使用该参数值）。注意：UDP 中该值决定了读取的最大数据量
     * @return UDP 服务端的响应结果
     */
    public static byte[] sendUdp(
        String ip, int port, byte[] data, int readTimeout,
        int bufferSize
    ) {
        // ❗️ 因为 UDP 只读取一次，故预设的缓冲区大小将影响能读取的最大数据量
        if (bufferSize < BUFFER_SIZE) {
            bufferSize = BUFFER_SIZE;
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(
                data, data.length, address, port
            );
            socket.send(packet);

            socket.setSoTimeout(readTimeout);
            return receivePacket(socket, packet, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(
                "UDP 通信失败 [ip=%s; port=%s]：%s".formatted(
                    ip, port, e.getMessage()
                )
            );
        }
    }

    private static byte[] readSocket(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int lengthRead;
        while ((lengthRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, lengthRead);
        }
        return output.toByteArray();
    }

    private static byte[] receivePacket(
        DatagramSocket socket, DatagramPacket packet, int bufferSize
    ) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        packet.setData(new byte[bufferSize]);

        socket.receive(packet);
        output.write(packet.getData(), packet.getOffset(), packet.getLength());
        return output.toByteArray();
    }
}
