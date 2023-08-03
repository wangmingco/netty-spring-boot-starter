import io.netty.channel.socket.DatagramPacket;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author ming.wang
 * @date 2023/8/3
 */
public class UDPServer {

    public static void main(String[] args) throws Exception {
        //1.创建服务端+端口
        DatagramSocket server = new DatagramSocket(7777);
        //2.准备接受容器
        byte[] container = new byte[1024];
        //3.封装成包
        DatagramPacket packet = new DatagramPacket(container, container.length);
        //4.接受数据
        server.receive(packet);
        //5.分析数据
        byte[] data = packet.getData();
        int len = packet.getLength();
        System.out.println("server receive:"+new String(data,0,len));
        //6.释放
        server.close();
    }
}
