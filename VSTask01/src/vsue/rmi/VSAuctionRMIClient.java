package vsue.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UnknownFormatConversionException;


public class VSAuctionRMIClient implements VSAuctionEventHandler {

	/* The user name provided via command line. */
	private final String userName;
	
	private VSAuctionService auctionServer = null;
	private VSAuctionEventHandler thisRemote = null;
	
	private final int THIS_REMOTE_PORT = 0;
	
	
	public VSAuctionRMIClient(String userName) {
		this.userName = userName;
	}

	
	// #############################
	// # INITIALIZATION & SHUTDOWN #
	// #############################

	public void init(String registryHost, int registryPort) throws RemoteException, NotBoundException {
        thisRemote = (VSAuctionEventHandler) UnicastRemoteObject.exportObject(this, THIS_REMOTE_PORT);
		
		Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
		auctionServer = (VSAuctionService) registry.lookup(VSAuctionRMIServer.REGISTRY_NAME);
		
	}
	
	public void shutdown() {
		/*
		 * TODO: Implement client shutdown code
		 */
	}
	
	
	// #################
	// # EVENT HANDLER #
	// #################

	@Override
	public void handleEvent(VSAuctionEventType event, VSAuction auction) {
		/*
		 * TODO: Implement event handler
		 */
	}
	
	
	// ##################
	// # CLIENT METHODS #
	// ##################

	public void register(String auctionName, int duration, int startingPrice) {

		VSAuction auction = new VSAuction(auctionName, startingPrice);
		
		try {
			auctionServer.registerAuction(auction, duration, thisRemote);
			System.out.printf("registered auction %s\n", auction);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (VSAuctionException e){
			System.err.println(e.getMessage());
		}
	}
	
	public void list(){
		
		VSAuction[] auctions;
		try {
			auctions = this.auctionServer.getAuctions();

			if(auctions.length == 0){
				System.out.println("There are no auctions running");
				return;
			}
			
			System.out.println("following auctions are running:");
			for(VSAuction auction : auctions){
				System.out.printf("    - %s\n", auction);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void bid(String auctionName, int price) {

		final String SUCCESSFUL_BID_REPLY = "Your bid has been accepted",
				FAILED_BID_REPLY = "Your bid has been rejected";
		
		try {
			boolean successfulBid;
			successfulBid = this.auctionServer.placeBid(userName, auctionName, price, this);
		
			System.out.println(successfulBid ? SUCCESSFUL_BID_REPLY : FAILED_BID_REPLY);
			
		} catch (RemoteException e){
			e.printStackTrace();
		}
		catch(VSAuctionException e) {
			System.err.println(e.getMessage());
        }
	}

	
	// #########
	// # SHELL #
	// #########

	public void shell() {
		// Create input reader and process commands
		BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			// Print prompt
			System.out.print("> ");
			System.out.flush();
			
			// Read next line
			String command = null;
			try {
				command = commandLine.readLine();
			} catch(IOException ioe) {
				break;
			}
			if(command == null) break;
			if(command.isEmpty()) continue;
			
			// Prepare command
			String[] args = command.split(" ");
			if(args.length == 0) continue;
			args[0] = args[0].toLowerCase();
			
			// Process command
			try {
				boolean loop = processCommand(args);
				if(!loop) break;
			} catch(IllegalArgumentException iae) {
				System.err.println(iae.getMessage());
			}
		}
		
		// Close input reader
		try {
			commandLine.close();
		} catch(IOException ioe) {
			// Ignore
		}
	}
	
	private boolean processCommand(String[] args) {
		switch(args[0]) {
		case "register":
		case "r":
			if(args.length < 3) throw new IllegalArgumentException("Usage: register <auction-name> <duration> [<starting-price>]");
			int duration = Integer.parseInt(args[2]);
			int startingPrice = (args.length > 3) ? Integer.parseInt(args[3]) : 0;
			register(args[1], duration, startingPrice);
			break;
		case "list":
		case "l":
			list();
			break;
		case "bid":
		case "b":
			if(args.length < 3) throw new IllegalArgumentException("Usage: bid <auction-name> <price>");
			int price = Integer.parseInt(args[2]);
			bid(args[1], price);
			break;
		case "exit":
		case "quit":
		case "x":
		case "q":
			return false;
		default:
			throw new IllegalArgumentException("Unknown command: " + args[0]);
		}
		return true;
	}

	
	// ########
	// # MAIN #
	// ########
	
	public static void main(String[] args) {
		// Check arguments
		if(args.length < 3) {
			System.err.println("usage: java " + VSAuctionRMIClient.class.getName() + " <user-name> <registry_host> <registry_port>");
			System.exit(1);
		}

		// Create and execute client
		VSAuctionRMIClient client = new VSAuctionRMIClient(args[0]);
		try{
			client.init(args[1], Integer.parseInt(args[2]));
		} catch(RemoteException | NotBoundException e){
			e.printStackTrace();
			System.exit(1);;
		}
		client.shell();
		client.shutdown();
	}

}
