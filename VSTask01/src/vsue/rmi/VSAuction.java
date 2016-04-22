package vsue.rmi;

import java.io.Serializable;


public class VSAuction implements Serializable {

	/* The auction name. */
	private final String name;
	
	/* The currently highest bid for this auction. */
	private int price;

	//private long runningUntilMillis;
	private transient long runningUntilMillis;

	
	public VSAuction(String name, int startingPrice) {
		this.name = name;
		this.price = startingPrice;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}
	
	
	public void setRunningUntilMillis(long millis){
		runningUntilMillis = millis;
	}
	
	public long getRunningUntilMillis(){
		return runningUntilMillis;
	}
	
	public boolean isRunning(){

		boolean stillRunning = System.currentTimeMillis() < runningUntilMillis;
		return stillRunning;
	}
	
	@Override
	public String toString(){
		return String.format("%s (%d)", getName(), getPrice());
	}
	
	@Override
	public boolean equals(Object other){
		return this.getName().compareTo(((VSAuction) other).getName()) == 0;
	}
	
}
