package vsue.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VSServer {
    
    private List<Thread> listeners = new LinkedList<Thread>();
    
    private final long SHUTDOWN_GRACE = 300;
    
    public void serve(VSRemoteReference reference) throws IOException{
        Thread listener = new Thread(new RequestListener(reference));
        listener.start();
        listeners.add(listener);
    }
    
    public void shutdown(){
        for(Thread listener : listeners){
            listener.interrupt();
            try{
                listener.join(SHUTDOWN_GRACE);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    
    private static class RequestListener implements Runnable {
        private ServerSocket listen;
        public static final int RANDOM_PORT = 0;
        public RequestListener(VSRemoteReference reference) throws IOException{
            listen = new ServerSocket(RANDOM_PORT);
            int port = listen.getLocalPort();
            reference.setPort(port);
        }
        
        public void run(){
            try{
                
                while(true){
                    Socket incomming = listen.accept();
                    handleRequest(incomming);
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

        private void handleRequest(Socket incomming){
            Thread worker = new Thread(new RequestHandler(incomming));
            
            worker.start();
        }
    }

    private static class RequestHandler implements Runnable {
        
        private final VSObjectConnection objConn;
        
        private int objId;
        private String methodName;
        private Object[] args;
        
        private Object result;
        private Throwable thrownException;
        
        private boolean resultIsAnException(){
            return thrownException != null;
        }
        
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
                
            } catch (Throwable t) {
                t.printStackTrace();
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
            try{
                result = objManager.invokeMethod(objId, methodName, args);
            } catch (Throwable t){
                thrownException = t;
            }
        }
        
        private void sendResponse() throws Throwable {
            
            VSResponse response ;
            if(resultIsAnException()){
                response = VSResponse.createErrorResponse(thrownException);
            }
            else{
                response = VSResponse.createResponse(result);
            }
            objConn.sendObject(response);
        }
    }
}
