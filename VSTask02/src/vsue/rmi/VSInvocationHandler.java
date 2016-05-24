package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;

public class VSInvocationHandler implements InvocationHandler, Serializable {

    
    private VSRemoteReference remoteReference;
    
    public VSInvocationHandler(VSRemoteReference remoteReference){
        this.remoteReference = remoteReference;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        VSObjectConnection con = null;
        Object result = null;
        
        try{
             con = remoteReference.openNewConnection();
             
             VSRequest request = new VSRequest(
                 remoteReference.getObjectID(),
                 method.getName(),
                 args
             );
             
             con.sendObject(request);
             
             VSResponse response = (VSResponse) con.receiveObject();
             result = response.getResult();
             
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(con != null){
                try{
                con.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        
        return result;
    }
}
