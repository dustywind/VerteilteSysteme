package vsue.rmi;

import java.io.Serializable;
import java.util.Arrays;

public class VSTestMessage implements Serializable, Comparable<VSTestMessage>{

    private int integer;
    private String string;
    private Object[] objects;
    
    public VSTestMessage(int integer, String string, Object[] objects){
        this.integer = integer;
        this.string = string;
        this.objects = objects;
    }
    
    @Override
    public boolean equals(Object other){
        return this.compareTo((VSTestMessage) other) == 0;
    }

    @Override
    public int compareTo(VSTestMessage other) {
        
        if(integer != other.integer){
            return integer = other.integer;
        }
        int strCmp = string.compareTo(other.string);
        if(strCmp != 0){
            return strCmp;
        }
        
        int arrayCmp = objects.length - other.objects.length;
        if(arrayCmp != 0){
            return arrayCmp;
        }
        
        for(int i = 0; i < objects.length; ++i){
            if(objects[i] != other.objects[i]){
                return -1;
            }
        }
        
        return 0;
    }
    

    @Override
    public String toString(){
        return String.format("VSTestMessage: %d; %s; %s",
                integer, string, Arrays.toString(objects));
    }
}
