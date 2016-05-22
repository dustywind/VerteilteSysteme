package vsue.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class VSResponse implements Serializable, Externalizable {
    
    private Object result;
    private Throwable error = null;
    
    public VSResponse(){
        
    }
    
    private boolean hasError(){
        return error != null;
    }
    
    public static VSResponse createResponse(Object result){
        VSResponse response = new VSResponse();
        response.result = result;
        return response;
    }
    public static VSResponse createErrorResponse(Throwable error){
        VSResponse response = new VSResponse();
        response.error = error;
        return response;
    }
    
    public Object getResult() throws Throwable {
        if(hasError()){
            throw error;
        }
        return result;
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException,
            ClassNotFoundException {
        result = oi.readObject();
        error = (Throwable) oi.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeObject(result);
        oo.writeObject(error);
    }
}