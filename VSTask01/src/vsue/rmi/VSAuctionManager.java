package vsue.rmi;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VSAuctionManager {

    private Thread cron;
    List<AuctionWrapper> auctions = new LinkedList<AuctionWrapper>();

    
    {
        cron = new Thread(new CronGarbageCollector(this));
        cron.start();
    }
    
    public void addAuction(VSAuction auction, int duration, VSAuctionEventHandler handler) 
            throws VSAuctionException{
        
        AuctionWrapper wrappedAuction = new AuctionWrapper(auction, duration, handler);
        addToAuctionList(wrappedAuction);
    }
    
    private synchronized void addToAuctionList(AuctionWrapper auction) throws VSAuctionException{
        if(auctions.contains(auction)){
            throw new VSAuctionException("Auction does already exist");
        }
        auctions.add(auction);
    }
    
    public synchronized VSAuction[] getAuctionsAsArray(){
        VSAuction[] auctionArray = new VSAuction[auctions.size()];
        
        int index = 0;
        for(AuctionWrapper auction : auctions){
            auctionArray[index++] = auction.getAuction();
        }
        
        return auctionArray;
    }
    
    public synchronized boolean placeBid(String user, String auctionName, int price, VSAuctionEventHandler handler)
            throws VSAuctionException{
        AuctionWrapper requestedAuction = getAuctionByName(auctionName);
        
        if(requestedAuction == null){
            throw new VSAuctionException("Auction not found");
        }
        
        return requestedAuction.bid(price, handler);
    }
    
    private synchronized AuctionWrapper getAuctionByName(String auctionName){
        for(AuctionWrapper auction : auctions){
            if(auction.getName().compareTo(auctionName) == 0){
                return auction;
            }
        }
        return null;
    }
    
    
    private static class AuctionWrapper extends VSAuction {
        
        private final static Logger LOGGER = Logger.getLogger(AuctionWrapper.class.getName());
        static {
            LOGGER.setLevel(Level.INFO);
        }
        
        private VSAuction auction;
        
        private VSAuctionEventHandler owner;
        private VSAuctionEventHandler highestBidder = null;
        
        private int timeToLive;

        AuctionWrapper(VSAuction auction, int duration, VSAuctionEventHandler owner){
            super(auction.getName(), auction.getPrice());
            
            this.auction = auction;
            this.owner = owner;
            this.timeToLive = duration;
        }
        
        VSAuctionEventHandler getOwner(){
            return owner;
        }
        
        VSAuctionEventHandler getHighestBidder(){
            return highestBidder;
        }
        
        @Override
        public String getName(){
            return auction.getName();
        }
        
        @Override
        public int getPrice(){
            return auction.getPrice();
        }
        
        public VSAuction getAuction(){
            return auction;
        }
        
        void decrementTimeToLive(){
            timeToLive -= 1;
            if(!isStillRunning()){
                endAuction();
            }
        }
        
        void endAuction(){
            try{
                owner.handleEvent(VSAuctionEventType.AUCTION_END, this.auction);
                if(highestBidder != null){
                    highestBidder.handleEvent(VSAuctionEventType.AUCTION_WON, this.auction);
                }
            }catch(RemoteException e){
                LOGGER.severe(
                    String.format("Could not properly end auction \"%s\"", this.auction)
                );
            }
        }
        
        boolean isStillRunning(){
            return timeToLive > 0;
        }
        
        boolean bid(int price, VSAuctionEventHandler handler){
            boolean bidIsHigher = price > getPrice();
            if(bidIsHigher){
                if(highestBidder != null){
                    try {
                        highestBidder.handleEvent(VSAuctionEventType.HIGHER_BID, this.auction);
                    } catch (RemoteException e) {
                        LOGGER.severe(
                            String.format("Could not inform user about change in auction \"%s\"", this)
                        );
                    }
                }
                highestBidder = handler;
            }
            return bidIsHigher;
        }
        
        @Override
        public boolean equals(Object other){
            return auction.equals(((AuctionWrapper)other).auction);
        }
    }
    
    private static class CronGarbageCollector implements Runnable {

        private VSAuctionManager auctionManager;
        
        public CronGarbageCollector(VSAuctionManager auctionManager){
            this.auctionManager = auctionManager;
        }
        
        @Override
        public void run() {
            try{
                while(true){
                    
                    Thread.sleep(1000);
                    
                    synchronized(auctionManager){
                        Iterator<AuctionWrapper> auctionIter = auctionManager.auctions.iterator();
                        while(auctionIter.hasNext()){
                            AuctionWrapper current = auctionIter.next();
                            
                            current.decrementTimeToLive();
                            
                            if(!current.isStillRunning()){
                                auctionIter.remove();
                            }
                        }
                    }
                }
                
            } catch (InterruptedException e){
                // thread is going to shut down
            }
        }
        
    }
}
