package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class ServerFileManager {
    private SocketChannel client;
    private ByteBuffer buffer;

    public ServerFileManager(SocketChannel client, ByteBuffer buffer) {
        this.client = client;
        this.buffer = buffer;
    }

    public void writeFile(String fileName) {
        try (FileChannel writer =
                     FileChannel.open(Paths.get(fileName),
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFilesList(String fileName) {
        try {
            List<String> paths = Files.walk(Paths.get(fileName))
                    .map(Path::toString)
                    .collect(Collectors.toList());

            StringBuilder allPaths = new StringBuilder("send_files_list\n");
            for (String path : paths) {
                allPaths.append(path).append("\n");
            }
            ByteBuffer buffer = ByteBuffer.wrap(allPaths.toString().getBytes());
            client.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String path) {
        try (FileChannel channel = FileChannel.open(Paths.get(path))) {
            ByteBuffer headerBuffer = ByteBuffer.wrap(("send_file " + path + "\n").getBytes());
            client.write(headerBuffer);

            buffer.clear();
            while ((channel.read(buffer)) != -1) {
                buffer.flip();
                client.write(buffer);
                buffer.clear();
            }
            System.out.println("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String path){
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
