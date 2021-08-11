package server;

import handlers.MessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {
    private Selector selector;
    private final InetSocketAddress address;

    public Server(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    public void start() throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(address);

        serverSocket.configureBlocking(false);

        //Селектор будет прослушивать новые подключения на сервер
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started");

        while (true) {
            selector.select();

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                if (key.isAcceptable()) {
                    accept(serverSocket);
                }
                if (key.isReadable()) {
                    read(key);
                }
                if (key.isWritable()) {
                    write(key);
                }

                keys.remove();
            }
        }

    }

    private void accept(ServerSocketChannel server) throws IOException {
        //новый клиент
        SocketChannel channel = server.accept();
        System.out.println("Client connected. IP:" + channel.getLocalAddress());

        //неблокирующий режим
        channel.configureBlocking(false);

        //регистрируем канал для чтения
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024*1024);

        MessageHandler handler = new MessageHandler(client, buffer);

        String operation = handler.getOperation();
        String filename = handler.getFilename();


        ServerFileManager fileManager = new ServerFileManager(client, buffer);

        switch (operation){
            case ("write_file") :
                fileManager.writeFile(filename);
            case ("download_file") :
                fileManager.sendFile(filename);
            case ("print_list") :
                fileManager.sendFilesList(filename);
        }

        client.register(selector, SelectionKey.OP_READ);
    }

    private void write(SelectionKey key) throws IOException {

    }

    public static void main(String[] args) {
        try {
            new Server("localhost", 8090).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
