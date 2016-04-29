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
                
                Thread worker = new Thread(new VSServerWorker(incomming));
                
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
    
    private static class VSServerWorker implements Runnable {
        
        private Socket connection;
        private VSObjectConnection objConn;
        
        public VSServerWorker(Socket connection){
            this.connection = connection;
            VSConnection conn = new VSConnection(this.connection);
            objConn = new VSObjectConnection(conn);
        }

        @Override
        public void run() {
            try {
                Serializable o = (Serializable) objConn.receiveObject();
                
                objConn.sendObject(o);
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                
                try {
                    this.connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            return;
        }
        
        
    }
}
