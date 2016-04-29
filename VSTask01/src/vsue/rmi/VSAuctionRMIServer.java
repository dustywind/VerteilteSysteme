package vsue.rmi;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import vsue.rmi.VSConfig;

public class VSAuctionRMIServer extends VSAuctionServerImpl {
    
    public static void main(String[] args){
        
        
        VSAuctionService auction = new VSAuctionServerImpl();
        
        
        try {
            VSAuctionService remoteAuction = 
                    (VSAuctionService) UnicastRemoteObject.exportObject(auction, VSConfig.Rmi.SELECTED_PORT);
            Registry registry = LocateRegistry.createRegistry(VSConfig.Rmi.REGISTRY_PORT);
            registry.bind(VSConfig.Rmi.REGISTRY_NAME, remoteAuction);
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
    }
}
