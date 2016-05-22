package vsue.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class VSRequest implements Serializable/*, Externalizable */{
    
    private transient final Charset charset = StandardCharsets.UTF_8;
    
    private int objId;
    private String methodName;
    private Object[] args;
    
    public int getObjectId(){
        return objId;
    }
    public String getGenericMethodName(){
        return methodName;
    }
    
    public Object[] getArgs(){
        return args;
    }
    
    public VSRequest(){
        
    }
    
    public VSRequest(int objId, String methodName, Object[] args){
        this.objId = objId;
        this.methodName = methodName;
        this.args = args;
    }
    
    /*
    @Override
    public void readExternal(ObjectInput oi) throws IOException,
            ClassNotFoundException {
        // read objId
        objId = oi.readInt();
        
        // read methodName
        int stringLength = oi.readInt();
        if(stringLength >= 0){
            byte[] stringAsBytes = new byte[stringLength];
            oi.read(stringAsBytes, 0, stringLength);
            methodName = new String(stringAsBytes, charset);
        }
        else{
            methodName = null;
        }
        
        // read args
        short objectsCount = oi.readShort();
        if(objectsCount >= 0){
            args = new Object[objectsCount];
            for(int i = 0; i < objectsCount; ++i){
                args[i] = oi.readObject();
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        // write objectId
        oo.writeInt(objId);
        
        // write methodName
        if(methodName != null){
            byte[] stringAsBytes = methodName.getBytes(charset);
            int stringLength = stringAsBytes.length;
            oo.writeInt(stringLength);
            oo.write(stringAsBytes);
        } else{
            oo.writeInt(-1);
        }
        
        // write  args
        if(args != null){
            short objectsCount = (short) args.length;
            oo.writeShort(objectsCount);
            for(Object o : args){
                oo.writeObject(o);
            }
        }else{
            oo.writeShort((short)-1);
        }
    }
    */
}