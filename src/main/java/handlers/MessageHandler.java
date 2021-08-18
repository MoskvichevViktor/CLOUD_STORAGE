package handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler {
    private String header;

    public MessageHandler(SocketChannel client, ByteBuffer buffer) {
        List<Byte> bytes = new ArrayList<>();
        try {
            client.read(buffer);

            buffer.flip();
            while(buffer.hasRemaining()){
                byte symbol = buffer.get();
                if((char) symbol == '\n'){
                    break;
                }
                bytes.add(symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] primitiveArr = new byte[bytes.size()];

        for(int i =0; i<bytes.size(); i++){
            primitiveArr[i] = bytes.get(i);
        }

        this.header = new String(primitiveArr);
    }

    public String getOperation(){
        if (header.contains(" "))
            return header.substring(0, header.indexOf(" ")).trim();
        return header.trim();
    }



    public String getFilename(){
        if (header.contains(" "))
            return header.substring(header.indexOf(" ")).trim();
        return "none";
    }

}
