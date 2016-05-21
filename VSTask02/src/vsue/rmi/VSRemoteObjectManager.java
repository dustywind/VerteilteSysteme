package vsue.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.HashMap;

public class VSRemoteObjectManager{
    
    private static final VSRemoteObjectManager singleton = new VSRemoteObjectManager();
    
    private final ExportedObjectStorage STORAGE = new ExportedObjectStorage();
    
    public static VSRemoteObjectManager getInstance(){
        return singleton;
    }
    
    public Remote exportObject(Remote obj) {
        Remote proxy;
        
        if(!STORAGE.containsObject(obj)){
            proxy = proxify(obj);
        }else{
            proxy = STORAGE.getById(ExportedObjectStorage.objectToId(obj));
        }

        return proxy;
    }
    
    private Remote proxify(Remote obj) {
        
        Remote proxy;
        
        VSRemoteReference remoteReference = new VSRemoteReference(obj);
        VSInvocationHandler handler = new VSInvocationHandler(remoteReference);

        ClassLoader cl = Remote.class.getClassLoader();
        Class<?>[] intfs = new Class<?>[]{Remote.class};
        
        proxy = (Remote) Proxy.newProxyInstance(cl, intfs, handler);

        return proxy;
    }
    
    
    public Object invokeMethod(int objectID, String genericMethodName, Object[] args){

        Object result = null;
        Remote obj = STORAGE.getById(objectID);
        
        if(obj == null){
            return result;
        }
        
        try{
            Class<?>[] paramTypes = new Class<?>[args.length];
            for(int i = 0; i < args.length; ++i){
                paramTypes[i] = args[i].getClass();
            }
            
            Method method = Remote.class.getMethod(genericMethodName, paramTypes);
            
            result = method.invoke(obj, args);
            
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    private static class ExportedObjectStorage{
        private final HashMap<Integer, Remote> STORAGE = new HashMap<Integer, Remote>();

        public static int objectToId(Remote obj){
            return obj.hashCode();
        }
        
        public void insert(int objId, Remote obj){
            STORAGE.put(objId, obj);
        }
        
        public Remote getById(int objId){
            return STORAGE.get(objId);
        }
        
        public void removeById(int objId){
            STORAGE.remove(objId);
        }
        
        public boolean containsId(int objId){
            return STORAGE.containsKey(objId);
        }
        
        public boolean containsObject(Remote obj){
            return STORAGE.containsKey(obj.hashCode());
        }
    }
}