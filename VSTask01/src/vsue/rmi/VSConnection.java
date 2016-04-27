package vsue.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class VSConnection {
    
    private Socket connection;
    
    public VSConnection(Socket openConnection) {
        connection = openConnection;
    }

    public void sendChunk(byte[] chunk) throws IOException {
        
        OutputStream outStream = connection.getOutputStream();
        outStream.write(chunk);
    }
    
    public byte[] receiveChunk() throws IOException {
        
        InputStream inStream = connection.getInputStream();
        
        List<Byte> partialChunks = new LinkedList<Byte>();
        
        int readByte = 0;
        while((readByte = inStream.read()) > 0) {
            byte partialChunk = (byte) readByte;
            partialChunks.add(partialChunk);
        }
        
        byte[] byteArray = new byte[partialChunks.size()];
        
        int index = 0;
        for(byte b : partialChunks){
            byteArray[index++] = b;
        }
        
        return byteArray;
    }
}
