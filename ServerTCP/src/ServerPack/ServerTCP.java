package ServerPack;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class ServerTCP {
    public static void main(String[] args) {
        new ServerTCP().start();
    }

    private final String adr = "localhost";
    private final int port = 3030;
    private byte[] ba;

    private final void start(){
        try(Selector selector = Selector.open();
            ServerSocketChannel socket = ServerSocketChannel.open()
        ){
            InetSocketAddress inetSocketAddress = new InetSocketAddress(adr, port);

            socket.bind(inetSocketAddress, 2);
            socket.configureBlocking(false);

            int ops = socket.validOps();
            SelectionKey key = socket.register(selector, ops);

            System.out.println("start. server -> waiting for client");
            while (true){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey selKey = iterator.next();
                    iterator.remove();
                    if(selKey.isAcceptable()) {
                        SocketChannel client = socket.accept();
                        client.configureBlocking(false);
                        client.register(selector, OP_READ);
                        System.out.println("server -> client connected: " + client.getRemoteAddress());
                    }else if(selKey.isReadable()){
                        SocketChannel client = (SocketChannel) selKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(256); // создаем пустой буфер размером 256 байт
                        client.read(buffer);  // данные перемещаются: сокет --> буфер
                        String s = new String(buffer.array()).trim();  // превращаем буфер в строку
                        System.out.println("server get from client -> " + s);
                        ba = s.getBytes(StandardCharsets.UTF_8);
                        if(s.equalsIgnoreCase("/q")){
                            client.close();
                            System.out.println("server -> connection closed");
                            System.out.println("server -> waiting for client");
                        }
                        else client.register(selector, OP_WRITE);
                    }
                    else if(selKey.isWritable()){
                        SocketChannel client = (SocketChannel) selKey.channel();
//                        String s = "java";
//                        ba = s.getBytes(StandardCharsets.UTF_8);
                        ByteBuffer buffer = ByteBuffer.wrap(ba);
                        client.write(buffer);
//                        client.close();
                        System.out.println("server -> waiting for client");
                        client.register(selector, OP_READ);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
