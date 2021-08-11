package client;

import handlers.ClientInputHandler;
import handlers.MessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Client {
    private Selector selector;
    private SocketChannel client;
    private ByteBuffer buffer;
    private final InetSocketAddress address;
    private String userCatalog = "user/";

    public Client(String host, int port) {
        this.address = new InetSocketAddress(host, port);
        buffer = ByteBuffer.allocateDirect(1024*1024);
    }

    public void start() throws IOException {
        selector = Selector.open();
        client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(address);

        client.register(selector, SelectionKey.OP_CONNECT);


        while (true) {
            selector.select();

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                if (key.isConnectable()) {
                    connect();
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

    private void connect() throws IOException {
        if (client.isConnectionPending()) {
            client.finishConnect();
            client.register(selector, SelectionKey.OP_WRITE);
        }
    }

    private void write(SelectionKey key) throws ClosedChannelException {
        SocketChannel client = (SocketChannel) key.channel();

        String choice = ClientInputHandler.getChoice();

        ClientFileManager fileManager = new ClientFileManager(client, buffer);

        switch (choice){
            case ("1") :
                fileManager.filesListRequest(userCatalog);
                client.register(selector, SelectionKey.OP_READ);

            case ("2") :
                String fileName = ClientInputHandler.getFilename();
                fileManager.uploadFile(userCatalog, fileName);
                client.register(selector, SelectionKey.OP_WRITE);

            /*case ("4") :
                String fileName = ClientInputHandler.getFilename();
                fileManager.downloadFileRequest(userCatalog + fileName);
                client.register(selector, SelectionKey.OP_READ);
            */
            default :
                System.out.println("");
        }
    }


    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        MessageHandler handler = new MessageHandler(client, buffer);

        ClientFileManager fileManager = new ClientFileManager(client, buffer);

        String operation = handler.getOperation();
        String filename = handler.getFilename();

        switch (operation){
            case ("send_file") :
                fileManager.writeFile(filename);

            case ("send_files_list") :
                fileManager.printFilesList();
        }


        client.register(selector, SelectionKey.OP_WRITE);
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", 8090).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
