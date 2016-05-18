package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

public class VSInvocationHandler implements InvocationHandler, Serializable {

    
    private VSRemoteReference remote;
    
    public VSInvocationHandler(VSRemoteReference remoteReference){
        this.remote = remoteReference;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        Object retVal = null;
        boolean argumentsAreValid = proxy != null 
                && method != null;
        
        if(argumentsAreValid){
            
            try{
                Socket s = establishConnection();
                VSObjectConnection objCon = createObjectConnection(s);
                
                int objId = remote.getObjectID();
                String methodName = method.getName();
                
                
                Object[] packed = packItems(objId, methodName, args);
                objCon.sendObject(packed);
                
                closeConnection(s);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
        }
        return retVal;
    }
    
    
    private static Object[] packItems(int objId, String methodName, Object[] args){
        Object[] packed = new Object[]{objId, methodName, args};
        return packed;
    }
        
    private static int getObjectIdFromPackedItem(Object[] packed){
        return (int) packed[0];
    }
    
    private static String getMethodNameFromPackedItem(Object[] packed){
        return (String) packed[1];
    }
    
    private static Object[] getArgsFromPackedItem(Object[] packed){
        return (Object[]) packed[2];
    }
    
    
    private Socket establishConnection() throws UnknownHostException, IOException{
        
        Socket s = new Socket(remote.getHost(), remote.getPort());
        return s;
    }
    
    private void closeConnection(Socket s){
        try{
            s.close();
        } catch(IOException e){
            // suppress exception
        }
    }
    
    private static VSObjectConnection createObjectConnection(Socket s){
        VSConnection connection = new VSConnection(s);
        VSObjectConnection objCon = new VSObjectConnection(connection);
        return objCon;
    }
    
    
    
}
