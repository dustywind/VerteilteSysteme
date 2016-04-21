package vsue.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;

public class VSAuctionServerImpl implements VSAuctionService {
	

	private ArrayList<VSAuction> runningAuctions = new ArrayList<VSAuction>();

	@Override
	public void registerAuction(VSAuction auction, int duration,
			VSAuctionEventHandler handler) throws VSAuctionException,
			RemoteException {
		
		boolean auctionAlreadyExists = runningAuctions.contains(auction);
		
		if(auctionAlreadyExists){
			throw new VSAuctionException("Auction does already exist");
		}
		
		runningAuctions.add(auction);
		auction.setRunningUntilMillis(System.currentTimeMillis() + duration * 1000);
	}

	@Override
	public synchronized VSAuction[] getAuctions() throws RemoteException {
		
		clearOldAuctions();
				
		return runningAuctions.toArray(new VSAuction[0]);
	}
	
	private synchronized void clearOldAuctions(){
		
		LinkedList<VSAuction> removeList = new LinkedList<VSAuction>();
		
		for(VSAuction auction : runningAuctions){
			if(!auction.isRunning()){
				removeList.add(auction);
			}
		}
		
		for(VSAuction auction : removeList){
			runningAuctions.remove(auction);
		}
	}
	
	
	private VSAuction getAuctionByName(String auctionName) throws RemoteException {
		VSAuction identifiedOne = null;
		
		for(VSAuction auction : getAuctions()){
			if(auction.getName().compareTo(auctionName) == 0){
				identifiedOne = auction;
			}
		}
		
		return identifiedOne;
	}
	

	@Override
	public boolean placeBid(String userName, String auctionName, int price,
			VSAuctionEventHandler handler) throws VSAuctionException,
			RemoteException {		
		
		VSAuction requestedAuction = getAuctionByName(auctionName);
		
		if(requestedAuction == null){
			throw new VSAuctionException("Auction not found");
		}
		
		boolean bidWasHigher = false;
		
		synchronized (requestedAuction){
			if(requestedAuction.getPrice() < price){
				bidWasHigher = true;
				requestedAuction.setPrice(price);
			}
		}
		
		return bidWasHigher;
	}

}
