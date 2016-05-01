package vsue.rmi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class VSObjectConnection {
    
    private VSConnection connection;
    
    private HexPrinter hexPrinter = new HexPrinter();
    
    private final int BUFFER_SIZE = 4096;
    
    public VSObjectConnection(VSConnection connection){
        this.connection = connection;
    }

    public void sendObject(Serializable object) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(BUFFER_SIZE);
        
        ObjectOutputStream objectStream = new ObjectOutputStream(outStream);
        
        objectStream.writeObject(object);
        
        byte[] bytesToSend = outStream.toByteArray();
        
        
        System.out.format("Sending object \"%s\"%s", object.toString(), System.lineSeparator());
        hexPrinter.print(bytesToSend);
        
        connection.sendChunk(bytesToSend);
    }
    
    public Serializable receiveObject() throws IOException, ClassNotFoundException, Exception{
        
        byte[] serializedObject = connection.receiveChunk();
        
        ByteArrayInputStream byteStream = 
                new ByteArrayInputStream(serializedObject);
        ObjectInputStream deserializer = new ObjectInputStream(byteStream);
        
        Serializable readObject = (Serializable) deserializer.readObject();
        
        return readObject;
    }
}
