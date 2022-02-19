package ClientPack;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientTCP {
    public static void main(String[] args) {
        new ClientTCP().start();
    }

    private final String adr = "localhost";
    private final int port = 3030;

    private final void start() {
        InetSocketAddress address = new InetSocketAddress(adr, port);
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            System.out.println("client -> started");
            Scanner scanner = new Scanner(System.in);
            String s;
            while (true) {
                System.out.println("client -> ");
                ByteBuffer buffer = ByteBuffer.allocate(256);
                s = scanner.nextLine();
//              buffer.put("client ".getBytes());
                if (s.equalsIgnoreCase("/q")) break;
                buffer.put(s.getBytes());
                buffer.flip();
//              System.err.println(new String(buffer.array(), buffer.position(), buffer.limit()));
                socketChannel.write(buffer);// данные перемещаются: буфер --> сокет
                buffer.clear();
                socketChannel.read(buffer);  // данные перемещаются: сокет --> буфер
                buffer.flip();
                s = new String(buffer.array(), buffer.position(), buffer.limit());
                System.out.println("client -> " + s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
