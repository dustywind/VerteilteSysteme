package vsue.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.logging.Level;

public class VSAuctionServerImpl implements VSAuctionService {
	
    private final static Logger LOGGER = Logger.getLogger(VSAuctionServerImpl.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }

	private ArrayList<VSAuction> runningAuctions = new ArrayList<VSAuction>();

	@Override
	public void registerAuction(VSAuction auction, int duration,
			VSAuctionEventHandler handler) throws VSAuctionException,
			RemoteException {

        LOGGER.info(String.format("registered auction (%)", auction));
		
		boolean auctionAlreadyExists = runningAuctions.contains(auction);
		
		if(auctionAlreadyExists){
            LOGGER.info("User tried to register an already existing auction");
			throw new VSAuctionException("Auction does already exist");
		}
		
		runningAuctions.add(auction);
		auction.setRunningUntilMillis(System.currentTimeMillis() + duration * 1000);
	}

	@Override
	public synchronized VSAuction[] getAuctions() throws RemoteException {

        LOGGER.info("looking up running auctions");

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

        if(removeList.size() > 0){
            LOGGER.info(
                String.format("clearing %d old auctions", removeList.size())
            );
        }
		
		for(VSAuction auction : removeList){
			runningAuctions.remove(auction);
		}
	}
	
	
	private VSAuction getAuctionByName(String auctionName) throws RemoteException {


        LOGGER.info(String.format("User requested Auction with called \"%s\"", auctionName));

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
