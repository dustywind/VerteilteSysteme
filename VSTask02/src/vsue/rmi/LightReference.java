package vsue.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.Charset;

public class LightReference implements Comparable<LightReference>, Externalizable {
    
    public String hostId;
    public int objId;
    
    public final static Charset UTF8 = Charset.forName("UTF-8");
    
    public LightReference(String hostId, Object reference) {
        this.hostId = hostId;
        objId = reference.hashCode();
    }

    @Override
    public int compareTo(LightReference other) {
        int comparison = hostId.compareTo(other.hostId);
        if(comparison == 0){
            comparison = objId - other.objId;
        }
        return comparison;
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException,
            ClassNotFoundException {
        int hostIdLength = oi.readInt();
        byte[] hostIdBytes = new byte[hostIdLength];
        oi.read(hostIdBytes);
        
        objId = oi.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        byte[] hostIdBytes = hostId.getBytes(UTF8);
        oo.writeInt(hostIdBytes.length);
        oo.write(hostIdBytes);
        
        oo.writeInt(objId);
    }
}