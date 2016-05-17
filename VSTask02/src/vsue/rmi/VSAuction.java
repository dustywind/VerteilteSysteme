package vsue.rmi;

import java.io.Serializable;


public class VSAuction implements Serializable {

    /* The auction name. */
    private final String name;
    
    /* The currently highest bid for this auction. */
    private int price;

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
    
    @Override
    public String toString(){
        return String.format("%s (%d)", getName(), getPrice());
    }
    
    @Override
    public boolean equals(Object other){
        return this.getName().compareTo(((VSAuction) other).getName()) == 0;
    }
}
