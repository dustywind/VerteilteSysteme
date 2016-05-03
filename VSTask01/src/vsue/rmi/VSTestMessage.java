package vsue.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class VSTestMessage implements 
    Serializable,
    Externalizable,
    Comparable<VSTestMessage>{

    private int integer = 0;
    private String string = null;
    private Object[] objects = null;
    
    private transient final Charset charset = StandardCharsets.UTF_8;
    
    public VSTestMessage(){}
    
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
        
        StringBuilder sb = new StringBuilder("VSTestMessage: ");
        sb.append(integer).append("; ");
        sb.append('"').append(string).append('"').append("; ");
        sb.append("[");
        if(objects != null){
            int i = 0;
            for(Object o : objects){
                if(i > 0){
                    sb.append(", ");
                }
                i += 1;
                sb.append(o.toString());
            }
        }
        sb.append("]");
        
        return sb.toString();
    }

    
    @Override
    public void readExternal(ObjectInput oi) throws IOException,
            ClassNotFoundException {
        
        integer = oi.readInt();
        
        int stringLength = oi.readInt();
        if(stringLength >= 0){
            byte[] stringAsBytes = new byte[stringLength];
            oi.read(stringAsBytes, 0, stringLength);
            string = new String(stringAsBytes, charset);
        }
        else{
            string = null;
        }
        
        short objectsCount = oi.readShort();
        if(objectsCount >= 0){
            objects = new Object[objectsCount];
            for(int i = 0; i < objectsCount; ++i){
                objects[i] = oi.readObject();
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        
        
        
        oo.writeInt(integer);
        
        if(string != null){
            byte[] stringAsBytes = string.getBytes(charset);
            int stringLength = stringAsBytes.length;
            oo.writeInt(stringLength);
            oo.write(stringAsBytes);
        } else{
            oo.writeInt(-1);
        }
        
        if(objects != null){
            short objectsCount = (short) objects.length;
            oo.writeShort(objectsCount);
            for(Object o : objects){
                oo.writeObject(o);
            }
        }else{
            oo.writeShort((short)-1);
        }
    }
}
