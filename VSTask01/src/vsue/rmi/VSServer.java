package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class VSServer {
    
    private final static String HOST = "localhost";
    private final static int PORT = 9247;

    public static void main(String[] args){
        
        try{
            ServerSocket listen = new ServerSocket(PORT);
            
            while(true){
                Socket incomming = listen.accept();
                
                Thread worker = new Thread(new VSServerWorker(incomming));
                worker.start();
                
            }
            
            
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static class VSServerWorker implements Runnable {
        
        private Socket connection;
        
        public VSServerWorker(Socket connection){
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                VSConnection conn = new VSConnection(this.connection);
                VSObjectConnection objConn = new VSObjectConnection(conn);

                Serializable o = (Serializable) objConn.receiveObject();
                objConn.sendObject(o);
                
            } catch (ClassNotFoundException | IOException e) {

                e.printStackTrace();
            }
            
            return;
        }
        
        
    }
}
