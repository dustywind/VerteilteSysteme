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

    private final VSAuctionManager auctionManager = new VSAuctionManager();
    
    @Override
    public void registerAuction(VSAuction auction, int duration,
            VSAuctionEventHandler handler) throws VSAuctionException,
            RemoteException {

        LOGGER.info(String.format("registered auction (%s)", auction));
        
        auctionManager.addAuction(auction, duration, handler);
    }

    @Override
    public synchronized VSAuction[] getAuctions() throws RemoteException {

        LOGGER.info("looking up running auctions");

        return auctionManager.getAuctionsAsArray();
    }
    
    @Override
    public boolean placeBid(String userName, String auctionName, int price,
            VSAuctionEventHandler handler) throws VSAuctionException,
            RemoteException {
        boolean success = auctionManager.placeBid(userName, auctionName, price, handler);

        return success;
    }

}
