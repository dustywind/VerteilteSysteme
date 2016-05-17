package vsue.rmi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class VSAuctionManager {

    private Thread cron;
    List<AuctionWrapper> auctions = new LinkedList<AuctionWrapper>();
    HashMap<String, AuctionWrapper> auctionsByName = new HashMap<String, AuctionWrapper>();
    
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
        if(auctionsByName.containsKey(auction.getName())){
            throw new VSAuctionException("Auction does already exist");
        }
        auctions.add(auction);
        auctionsByName.put(auction.getName(), auction);
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
        if(auctionsByName.containsKey(auctionName)){
            return auctionsByName.get(auctionName);
        }
        return null;
    }
    
    private static class AuctionWrapper extends VSAuction {

        private final VSAuction auction;
        
        private final VSAuctionEventHandler owner;
        private VSAuctionEventHandler highestBidder = null;
        
        private long validUntil;

        AuctionWrapper(VSAuction auction, int duration, VSAuctionEventHandler owner){
            super(auction.getName(), auction.getPrice());
            
            this.auction = auction;
            this.owner = owner;
            validUntil = System.currentTimeMillis() + 1000*duration;
        }
        
        @Override
        public String getName(){
            return auction.getName();
        }
        
        @Override
        public int getPrice(){
            return auction.getPrice();
        }
        
        @Override
        public void setPrice(int price){
            auction.setPrice(price);
        }
        
        public VSAuction getAuction(){
            return auction;
        }
        
        void checkStatus(){
            if(!isStillRunning()){
                endAuction();
            }
        }
        
        void endAuction(){

            VSEventNotifier.queueNotifification(owner, VSAuctionEventType.AUCTION_END, this.auction);
            if(highestBidder != null){
                VSEventNotifier.queueNotifification(highestBidder, VSAuctionEventType.AUCTION_WON, this.auction);
            }
        }
        
        boolean isStillRunning(){
            return System.currentTimeMillis() < validUntil;
        }
        
        boolean bid(int price, VSAuctionEventHandler handler){
            boolean bidIsHigher = price > getPrice();
            if(bidIsHigher){
                setPrice(price);
                if(highestBidder != null){
                    VSEventNotifier.queueNotifification(highestBidder, VSAuctionEventType.HIGHER_BID, this.auction);
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
                    
                    Thread.sleep(500);
                    
                    removeOldAuctions();
                }
                
            } catch (InterruptedException e){
                // thread is going to shut down
            }
        }
        
        private void removeOldAuctions(){
            
            synchronized(auctionManager){
                Iterator<AuctionWrapper> auctionIter = auctionManager.auctions.iterator();
                while(auctionIter.hasNext()){
                    AuctionWrapper current = auctionIter.next();
                    
                    current.checkStatus();
                    
                    if(!current.isStillRunning()){
                        auctionManager.auctionsByName.remove(current.getName());
                        auctionIter.remove();
                    }
                }
            }
        }
        
    }
}
