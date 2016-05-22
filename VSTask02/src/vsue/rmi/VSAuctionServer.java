package vsue.rmi;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import vsue.rmi.VSConfig;

public class VSAuctionRMIServer extends VSAuctionServerImpl {
    
    public static void main(String[] args){
        
        
        VSAuctionService auction = new VSAuctionServerImpl();
        
        
        try {
            
            String registryName = VSConfig.Rmi.REGISTRY_NAME;
            int selectedPort = VSConfig.Rmi.SELECTED_PORT;
            int registryPort = VSConfig.Rmi.REGISTRY_PORT;
            
            VSAuctionService remoteAuction = 
                    (VSAuctionService) UnicastRemoteObject.exportObject(auction, selectedPort);
            Registry registry = LocateRegistry.createRegistry(registryPort);
            registry.bind(registryName, remoteAuction);
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
    }
}
