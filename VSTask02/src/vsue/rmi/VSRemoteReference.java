package vsue.rmi;

import java.io.Serializable;

public class VSRemoteReference implements Serializable {

    private String host;
    private int port;
    private int objectID;
    
    
    public String getHost(){return host;}
    public int getPort(){return port;}
    public int getObjectID(){return objectID;}
    
    public VSRemoteReference(String host, int port, int objectID){
        this.host = host;
        this.port = port;
        this.objectID = objectID;
    }
    
}
