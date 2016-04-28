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
            
            System.out.println(testConnection(objConn));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static boolean testConnection(VSObjectConnection objConn) throws Exception{
        
        boolean success = true;
        
        //success &= testPrimitiveInteger(objConn);
        //success &= testWrappedInteger(objConn);
        //success &= testShortString(objConn);
        success &= testVeryLongString(objConn);
        
        return success;
    }
    
    private static boolean testPrimitiveInteger(VSObjectConnection objConn) throws Exception{
        objConn.sendObject(TestObjects.primitiveInteger);
        int receivedPrimitiveInteger = (Integer) objConn.receiveObject();
        return  receivedPrimitiveInteger == TestObjects.primitiveInteger;
    }
    
    private static boolean testWrappedInteger(VSObjectConnection objConn) throws Exception{
        objConn.sendObject(TestObjects.wrappedInteger);
        int receivedWrappedInteger = (Integer) objConn.receiveObject();
        return  receivedWrappedInteger == TestObjects.wrappedInteger;
    }
    
    private static boolean testShortString(VSObjectConnection objConn) throws Exception{
        objConn.sendObject(TestObjects.shortString);
        String receivedString = (String) objConn.receiveObject();
        return receivedString.compareTo(TestObjects.shortString) == 0;
    }
    
    private static boolean testVeryLongString(VSObjectConnection objConn) throws Exception{
        objConn.sendObject(TestObjects.veryLongString);
        String receivedString = (String) objConn.receiveObject();
        return receivedString.compareTo(TestObjects.veryLongString) == 0;
    }
    
}
