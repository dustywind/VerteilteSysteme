package vsue.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.logging.Level;

public class VSAuctionServerImpl implements VSAuctionService {
    
    private final VSAuctionManager auctionManager = new VSAuctionManager();
    
    @Override
    public void registerAuction(VSAuction auction, int duration,
            VSAuctionEventHandler handler) throws VSAuctionException,
            RemoteException {

        auctionManager.addAuction(auction, duration, handler);
    }

    @Override
    public VSAuction[] getAuctions() throws RemoteException {

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
