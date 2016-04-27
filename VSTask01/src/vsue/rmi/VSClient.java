package vsue.rmi;

import java.io.Serializable;
import java.net.Socket;

public class VSClient {

    private final static String HOST = "localhost";
    private final static int PORT = 9247;
    
    public static void main(String[] args){
        
        try{
            Socket s = new Socket(HOST, PORT);
            VSConnection conn = new VSConnection(s);
            VSObjectConnection objConn = new VSObjectConnection(conn);
            
            objConn.sendObject(TestObjects.primitiveInteger);
            int receivedPrimitiveInteger = (Integer) objConn.receiveObject();
            
            assert receivedPrimitiveInteger == TestObjects.primitiveInteger;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
