package vsue.rmi;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VSAuctionRMIServer extends VSAuctionServerImpl {
	
	private static final int RMI_SELECTED_PORT = 0;
	private static final int REGISTRY_PORT = 12345;
	public static final String REGISTRY_NAME = "auctionServer";
	
	public static void main(String[] args){
		
		
		VSAuctionService auction = new VSAuctionServerImpl();
		
		
		try {
			VSAuctionService remoteAuction = 
					(VSAuctionService) UnicastRemoteObject.exportObject(auction, RMI_SELECTED_PORT);
			Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
			registry.bind(REGISTRY_NAME, remoteAuction);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}
}
