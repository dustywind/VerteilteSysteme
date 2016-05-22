package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class VSServer {
    
    public static void main(String[] args) {
        ServerSocket listen = null;
        try{
             listen = new ServerSocket(VSConfig.CommunicationSystem.PORT);
            
            while(true){
                Socket incomming = listen.accept();
                
                Thread worker = new Thread(new RequestHandler(incomming));
                
                worker.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(listen != null){
                    listen.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    private static class RequestHandler implements Runnable {
        
        private final VSObjectConnection objConn;
        
        private int objId;
        private String methodName;
        private Object[] args;
        
        private Object result;
        
        public RequestHandler(Socket connection){
            VSConnection conn = new VSConnection(connection);
            objConn = new VSObjectConnection(conn);
        }

        @Override
        public void run() {
            
            try {

                receiveRequest();
                
                executeRequest();
                
                sendResponse();
                
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                
                try {
                    this.objConn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            return;
        }
        
        private void receiveRequest() throws ClassNotFoundException, IOException, Exception{
            VSRequest request = (VSRequest) objConn.receiveObject();
            
            objId = request.getObjectId();
            methodName = request.getGenericMethodName();
            args = request.getArgs();
        }
        
        private void executeRequest(){
            VSRemoteObjectManager objManager = VSRemoteObjectManager.getInstance();
            result = objManager.invokeMethod(objId, methodName, args);
        }
        
        private void sendResponse() throws Exception{
            VSResponse response = new VSResponse(result);
            objConn.sendObject(response);

        }
        
        
    }
}
