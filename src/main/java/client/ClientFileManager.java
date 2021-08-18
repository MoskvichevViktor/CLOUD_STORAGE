package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ClientFileManager {
    private SocketChannel client;
    private ByteBuffer buffer;

    public ClientFileManager(SocketChannel client, ByteBuffer buffer) {
        this.client = client;
        this.buffer = buffer;
    }

    public void uploadFile(String userDir, String path) {
        try (FileChannel channel = FileChannel.open(Paths.get(path))) {
            ByteBuffer headerBuffer = ByteBuffer.wrap(("write_file " + userDir + path + "\n").getBytes());
            client.write(headerBuffer);

            while (channel.read(buffer)!= -1) {
                buffer.flip();
                client.write(buffer);
                buffer.compact();
            }
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filesListRequest(String catalog) {
        ByteBuffer headerBuffer = ByteBuffer.wrap(("print_list " + catalog + "\n").getBytes());
        try {
            client.write(headerBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFileRequest(String catalog) {
        ByteBuffer headerBuffer = ByteBuffer.wrap(("download_file " + catalog + "\n").getBytes());
        try {
            client.write(headerBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFilesList(){
        try {
            List<Byte> bytesList = new ArrayList<>();
            while(buffer.hasRemaining()){
                bytesList.add(buffer.get());
            }

            byte[] primitiveArr = new byte[bytesList.size()];
            for(int i =0; i<bytesList.size(); i++){
                primitiveArr[i] = bytesList.get(i);
            }

            System.out.println(new String(primitiveArr));

            int bytes = client.read(buffer);
            while(bytes != -1 && bytes !=0){
                buffer.flip();
                byte[] message = new byte[bytes];
                buffer.get(message);
                bytes = client.read(buffer);
                System.out.println(new String(message));
                buffer.clear();
            }
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String path){
        try (FileChannel writer =
                     FileChannel.open(Paths.get(path).getFileName(),
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING,
                             StandardOpenOption.WRITE)) {
            writer.write(buffer);
            buffer.clear();
            int bytes = client.read(buffer);

            while (bytes != -1 && bytes != 0) {
                buffer.flip();
                writer.write(buffer);
                buffer.clear();
                bytes = client.read(buffer);
            }
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileRequest(String path){
        ByteBuffer headerBuffer = ByteBuffer.wrap(("delete_file " + path + "\n").getBytes());
        try {
            client.write(headerBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
