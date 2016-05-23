package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class VSRemoteReference implements Serializable {

    private String host;
    private int port;
    private int objectID;
    
    private static String LOCAL_HOST;
    private static final int DEFAULT_PORT = 0;
    
    static {
        try{
            LOCAL_HOST = InetAddress.getLocalHost().getHostName();
        }catch(Exception e){
            LOCAL_HOST = "localhost";
        }
    }
    
    public String getHost(){
        return host;
    }
    
    void setHost(String host){
        this.host = host;
    }
    
    public int getPort(){
        return port;
    }
    
    void setPort(int port){
        this.port = port;
    }
    
    public int getObjectID(){
        return objectID;
    }
    
    public VSRemoteReference(Object orig){
        this(orig, LOCAL_HOST, DEFAULT_PORT);
    }
    
    public VSRemoteReference(Object orig, int port){
        this(orig, LOCAL_HOST, port);
    }
    
    public VSRemoteReference(Object orig, String host, int port) {
        objectID = orig.hashCode();
        this.host = host;
        this.port = port;
    }
    
    public VSObjectConnection openNewConnection() throws UnknownHostException, IOException{
        Socket s = new Socket(host, port);
        
        VSConnection con = new VSConnection(s);
        VSObjectConnection objCon = new VSObjectConnection(con);
        
        return objCon;
    }
    
}
