package vsue.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class VSResponse implements Serializable, Externalizable {
    
    private Object result;
    
    public VSResponse(Object result){
        
    }
    
    public Object getResult(){
        return result;
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException,
            ClassNotFoundException {
        result = oi.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeObject(result);
    }
}