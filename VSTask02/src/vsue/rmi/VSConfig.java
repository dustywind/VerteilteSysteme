package vsue.rmi;

public class VSConfig {
    
    public static class Rmi{
        public final static int SELECTED_PORT = 0;
        public final static int REGISTRY_PORT = 12345;
        public final static String REGISTRY_NAME = "auctionServer";
        public final static int CLIENT_REMOTE_PORT = 0;
    }

    public static class CommunicationSystem {
        public final static String HOST = "localhost";
        public final static int PORT = 9247;
    }

}
