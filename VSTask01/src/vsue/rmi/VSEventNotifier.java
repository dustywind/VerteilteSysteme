package vsue.rmi;

import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VSEventNotifier {
    
    private static VSEventNotifier singleton = new VSEventNotifier();
    
    private BlockingQueue<NotificationInfo> openNotifications = new LinkedBlockingQueue<NotificationInfo>();
    
    private Thread supervisor;
    
    {
        supervisor = new Thread(new Supervisor(openNotifications));
        supervisor.start();
    }
    
    public static void queueNotifification(VSAuctionEventHandler handler, VSAuctionEventType event, VSAuction auction){
        singleton.queueNotification(new NotificationInfo(handler, event, auction));
    }
    
    private void queueNotification(NotificationInfo notInfo){
        try{
            openNotifications.add(notInfo);
        } catch(Exception e){
            // could not inform user about something
        }
    }
    
    private static class Notifier implements Runnable {
        
        private NotificationInfo info;
        
        public Notifier(NotificationInfo info){
            this.info = info;
        }

        @Override
        public void run() {
            try{
                info.handler.handleEvent(info.type, info.auction);
            }catch(RemoteException r){
                r.printStackTrace();
            }
        }
    }
    
    private static class Supervisor implements Runnable {
        
        public final long timeout = 1000;
        
        private BlockingQueue<NotificationInfo> notificationQueue;
        
        public Supervisor(BlockingQueue<NotificationInfo> notificationQueue){
            this.notificationQueue = notificationQueue;
        }
        
        @Override
        public void run(){
            
            try{
                while(true){

                    NotificationInfo info = notificationQueue.take();
                    
                    Thread notifier = new Thread(new Notifier(info));
                    notifier.start();
                    notifier.join(timeout);

                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    private static class NotificationInfo{
        public VSAuctionEventHandler handler;
        public VSAuctionEventType type;
        public VSAuction auction;
        
        public NotificationInfo(VSAuctionEventHandler handler, VSAuctionEventType type, VSAuction auction){
            this.handler = handler;
            this.type = type;
            this.auction = auction;
        }
    }

}
