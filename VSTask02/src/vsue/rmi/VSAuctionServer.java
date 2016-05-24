package vsue.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class VSAuctionServer extends VSAuctionServerImpl {
    
    final static String registryName = VSConfig.Rmi.REGISTRY_NAME;
    final static int selectedPort = VSConfig.Rmi.SELECTED_PORT;
    
    private static Registry registry;
    
    public static void main(String[] args){
        
        VSAuctionService auctionService = new VSAuctionServer();

        try{
            init();
            
            registerAuctionService(auctionService);
            
            waitForUserInput();
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            unregisterAuctionService(auctionService);
            shutdown();
        }
    }
    
    private static void init() throws RemoteException{
        registry = LocateRegistry.createRegistry(VSConfig.Rmi.REGISTRY_PORT);
    }
    
    private static void registerAuctionService(VSAuctionService auctionService){

        VSRemoteObjectManager objManager = VSRemoteObjectManager.getInstance();
        VSAuctionService remoteAuction = (VSAuctionService) objManager.exportObject(auctionService);

        try {
            registry.bind(registryName, remoteAuction);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void unregisterAuctionService(VSAuctionService auctionService){
        VSRemoteObjectManager objManager = VSRemoteObjectManager.getInstance();
        objManager.unexportObject(auctionService);
    }
    
    private static void shutdown(){
        shutdownRegistry();
    }
    
    private static void shutdownRegistry(){
        if(registry != null){
            try{
                for(String entry : registry.list()){
                    registry.unbind(entry);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    
    private static void waitForUserInput(){
        Scanner s = null;
        try{
            s = new Scanner(System.in);
            s.next();
        } catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(s != null){
                s.close();
            }
        }
    }
}
