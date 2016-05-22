package vsue.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VSServer {
    
    final static String registryName = VSConfig.Rmi.REGISTRY_NAME;
    final static int selectedPort = VSConfig.Rmi.SELECTED_PORT;
    final static int registryPort = VSConfig.Rmi.REGISTRY_PORT;
    
    public static void main(String[] args) {
        VSServer server = new VSServer();
        server.prepareRegistry();
        server.run();
    }
    
    private void prepareRegistry(){

        VSAuctionService auction = new VSAuctionServer();
        VSRemoteObjectManager objManager = VSRemoteObjectManager.getInstance();
        VSAuctionService remoteAuction = (VSAuctionService) objManager.exportObject(auction, selectedPort);

        try {
            Registry registry = LocateRegistry.createRegistry(registryPort);
            registry.bind(registryName, remoteAuction);
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    private void run(){
        ServerSocket listen = null;
        try{
            listen = new ServerSocket(VSConfig.CommunicationSystem.PORT);
            
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
