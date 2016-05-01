package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import vsue.rmi.TestObjects.TestObject;

public class VSClient {
    
    @SuppressWarnings("unchecked")
    private TestObjects.TestObject<Serializable>[] testObjs = new TestObjects.TestObject[]{
            TestObjects.wrappedInteger,
            TestObjects.shortString,
            TestObjects.veryLongString,
            TestObjects.intArray,
            TestObjects.auction
    };
    
    private final String host;
    private final int port;
    
    public VSClient(String host, int port){
        this.host = host;
        this.port = port;
    }
    
    public boolean runTests(){
        boolean success = true;
        
        for(TestObject<Serializable> tObj : testObjs){
            Socket connSock = null;
            try{
                connSock = new Socket(host, port);
                
                VSConnection conn = new VSConnection(connSock);
                VSObjectConnection objConn = new VSObjectConnection(conn);
                
                objConn.sendObject((Serializable) tObj.obj);
                Serializable reply = objConn.receiveObject();
                
                success &= tObj.objIsEqualTo(reply);
                
            }catch(Exception e){
                success = false;
                e.printStackTrace();
            }
            finally{
                try{
                    if(connSock != null){
                        connSock.close();
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        
        return success;
        
    }
    
    public static void main(String[] args){
        
        VSClient client = new VSClient(
                VSConfig.CommunicationSystem.HOST,
                VSConfig.CommunicationSystem.PORT
            );
        
        //client.test();
        client.sendTestMessages();
    }
    
    public void test(){
        boolean success = runTests();
        System.out.println(success ? "Success" : "Failed");
    }
    public void sendTestMessages(){
        
        VSTestMessage[] tests = new VSTestMessage[]{
                new VSTestMessage(1, "nummer eins", new Object[]{}),
                new VSTestMessage(2, "nummer zwei", new Object[]{1,2,3,4,5})
        };
        
        
        for(VSTestMessage msg : tests){
            Socket connSock = null;
            try{
                connSock = new Socket(host, port);
                
                VSConnection conn = new VSConnection(connSock);
                VSObjectConnection objConn = new VSObjectConnection(conn);
                
                objConn.sendObject((Serializable) msg);
                Serializable reply = objConn.receiveObject();
                
            }catch(Exception e){
                e.printStackTrace();
            }
            finally{
                try{
                    if(connSock != null){
                        connSock.close();
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        
        
    }
}
