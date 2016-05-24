package vsue.rmi;

import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class VSEventNotifier {
    
    private static VSEventNotifier singleton = new VSEventNotifier();
    
    private BlockingQueue<NotificationInfo> openNotifications = new LinkedBlockingQueue<NotificationInfo>();
    
    private Thread supervisor = null;
    private SupervisorConfig supervisorConfig = new SupervisorConfig();
    
    
    public static void queueNotifification(VSAuctionEventHandler handler, VSAuctionEventType event, VSAuction auction){
        
        if(handler != null && event != null && auction != null){
            singleton.queueNotification(new NotificationInfo(handler, event, auction));
            singleton.startSupervisorIfRequired();
        }
    }
    
    private synchronized void startSupervisorIfRequired(){
        if(supervisor == null){
            supervisor = new Thread(new Supervisor(supervisorConfig, openNotifications));
            supervisor.start();
        }
    }
    
    static synchronized boolean clearSupervisorIfPossible(){
        if(singleton.openNotifications.size() == 0){
            singleton.supervisor = null;
            return true;
        }
        return false;
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
        
        public final long queueTimeout = 500;
        public final long joinTimeout = 1000;
        
        private BlockingQueue<NotificationInfo> notificationQueue;
        private SupervisorConfig config;
        
        public Supervisor(SupervisorConfig config, BlockingQueue<NotificationInfo> notificationQueue){
            this.config = config;
            this.notificationQueue = notificationQueue;
        }
        
        @Override
        public void run(){
            
            try{
                config.isRunning = true;
                boolean shutdown = false;
                while(!shutdown){

                    NotificationInfo info = notificationQueue.poll(queueTimeout, TimeUnit.MILLISECONDS);
                    if(info != null){
                        Thread notifier = new Thread(new Notifier(info));
                        notifier.start();
                        notifier.join(joinTimeout);
                    }else{
                        boolean supervisorCleared = VSEventNotifier.clearSupervisorIfPossible();
                        shutdown = supervisorCleared;
                    }

                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    private static class SupervisorConfig {
        volatile boolean shutdown = false;
        volatile boolean isRunning;
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
