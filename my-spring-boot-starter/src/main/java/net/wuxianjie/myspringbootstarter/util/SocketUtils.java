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
    private static final int BUF_SIZE = 1024;

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

            OutputStream output = socket.getOutputStream();
            output.write(data);
            output.flush();

            socket.setSoTimeout(readTimeout);
            return readSocket(socket);
        } catch (IOException ex) {
            throw new RuntimeException(
                "TCP 通信失败 [ip=%s; port=%s]：%s".formatted(
                    ip, port, ex.getMessage()
                )
            );
        }
    }

    /**
     * 发送 UDP 数据包。
     *
     * <p>缓冲区 {@code bufSize} 的大小直接决定了能读取响应数据的最大长度。</p>
     *
     * @param ip UDP 服务端 IP
     * @param port UDP 服务端端口
     * @param data 要发送的数据
     * @param readTimeout 读取超时时间，单位：毫秒
     * @param bufSize 缓冲区大小（只有设置大于 {@link #BUF_SIZE} 才会使用该参数值），单位：字节。注意：UDP 中该值决定了读取的最大数据量
     * @return UDP 服务端的响应结果
     */
    public static byte[] sendUdp(
        String ip, int port, byte[] data, int readTimeout,
        int bufSize
    ) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress inetAddr = InetAddress.getByName(ip);
            DatagramPacket pkt = new DatagramPacket(
                data, data.length, inetAddr, port
            );
            socket.send(pkt);

            socket.setSoTimeout(readTimeout);
            return receivePkt(socket, pkt, bufSize);
        } catch (IOException ex) {
            throw new RuntimeException(
                "UDP 通信失败 [ip=%s; port=%s]：%s".formatted(
                    ip, port, ex.getMessage()
                )
            );
        }
    }

    private static byte[] readSocket(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[BUF_SIZE];
        int lenRead;
        while ((lenRead = input.read(buf)) != -1) {
            output.write(buf, 0, lenRead);
        }
        return output.toByteArray();
    }

    private static byte[] receivePkt(
        DatagramSocket socket, DatagramPacket pkt, int bufSize
    ) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // ❗️因为只读取一次，故预设的缓冲区大小将影响能读取的最多数据量
        if (bufSize < BUF_SIZE) {
            bufSize = BUF_SIZE;
        }
        pkt.setData(new byte[bufSize]);

        socket.receive(pkt);
        output.write(pkt.getData(), pkt.getOffset(), pkt.getLength());
        return output.toByteArray();
    }
}
