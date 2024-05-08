package net.wuxianjie.commonkit.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 网络 Socket 工具类。
 */
public class SocketUtils {

    /**
     * 缓存区大小，单位：字节。
     *
     * <p>1、TCP 通信时，该值决定了一次读取的最大数据量，且一定会读取完所有数据。</p>
     *
     * <p>2、UDP 通信时，该值决定了读取的最大数据量，因为仅读取一次。</p>
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 发送 TCP 数据包。
     *
     * <p>默认 2 秒的等待连接时间，5 秒的读取超时时间。</p>
     *
     * @param ip TCP 服务端 IP
     * @param port TCP 服务端端口
     * @param data 要发送的数据
     * @return TCP 服务端的响应结果
     */
    public static byte[] sendTcp(String ip, int port, byte[] data) {
        return sendTcp(ip, port, data, 2_000, 5_000);
    }

    /**
     * 发送 TCP 数据包。
     *
     * @param ip TCP 服务端 IP
     * @param port TCP 服务端端口
     * @param data 要发送的数据
     * @param connectTimeout 连接超时时间，单位：毫秒
     * @param readTimeout 读取超时时间，单位：毫秒
     * @return TCP 服务端的响应结果
     */
    public static byte[] sendTcp(
        String ip, int port, byte[] data,
        int connectTimeout, int readTimeout
    ) {
        try (Socket socket = new Socket()) {
            InetSocketAddress endpoint = new InetSocketAddress(ip, port);
            socket.connect(endpoint, connectTimeout);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();

            socket.setSoTimeout(readTimeout);
            return readSocket(socket);
        } catch (IOException e) {
            throw new RuntimeException(
                "TCP 通信失败 [ip=%s;port=%s]: %s".formatted(
                    ip, port, e.getMessage()
                )
            );
        }
    }

    /**
     * 发送 UDP 数据包。
     *
     * <p>默认读取超时时间为 2 秒。</p>
     *
     * @param ip UDP 服务端 IP
     * @param port UDP 服务端端口
     * @param data 要发送的数据
     * @return UDP 服务端的响应结果
     */
    public static byte[] sendUdp(String ip, int port, byte[] data) {
        return sendUdp(ip, port, data, 2_000);
    }

    /**
     * 发送 UDP 数据包。
     *
     * @param ip UDP 服务端 IP
     * @param port UDP 服务端端口
     * @param data 要发送的数据
     * @param readTimeout 读取超时时间，单位：毫秒
     * @return UDP 服务端的响应结果
     */
    public static byte[] sendUdp(
        String ip, int port, byte[] data, int readTimeout
    ) {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            InetAddress inetAddress = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(
                data, data.length, inetAddress, port
            );
            datagramSocket.send(packet);

            datagramSocket.setSoTimeout(readTimeout);
            return receivePacket(datagramSocket, packet);
        } catch (IOException e) {
            throw new RuntimeException(
                "UDP 通信失败 [ip=%s;port=%s]: %s".formatted(
                    ip, port, e.getMessage()
                )
            );
        }
    }

    private static byte[] readSocket(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int lengthRead;
        while ((lengthRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, lengthRead);
        }
        return outputStream.toByteArray();
    }

    private static byte[] receivePacket(
        DatagramSocket datagramSocket, DatagramPacket packet
    ) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // ❗️因为只读取一次，故预设的缓存区大小将影响能读取的最多数据量
        packet.setData(new byte[BUFFER_SIZE]);

        datagramSocket.receive(packet);
        outputStream.write(
            packet.getData(), packet.getOffset(), packet.getLength()
        );
        return outputStream.toByteArray();
    }

}
