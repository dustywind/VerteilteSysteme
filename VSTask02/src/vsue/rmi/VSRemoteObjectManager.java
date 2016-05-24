package vsue.rmi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VSRemoteObjectManager{
    
    private static final VSRemoteObjectManager singleton = new VSRemoteObjectManager();

    public static final int DEFAULT_PORT = 0;
    
    private VSServer server = null;
    
    //private final ExportedObjectStorage STORAGE = new ExportedObjectStorage();
    private final Map<Integer, Remote> PROXY_BY_ID = new HashMap<Integer, Remote>();
    //private final Map<Integer, Remote> ORIG_BY_PROXY_ID = new HashMap<Integer, Remote>();
    private final Map<Integer, VSRemoteReference> REMOTE_REFERENCE_BY_ID = new HashMap<Integer, VSRemoteReference>();
    
    public static VSRemoteObjectManager getInstance(){
        return singleton;
    }
    
    private static Integer calculateId(Object obj){
        return obj.hashCode();
    }
    
    public synchronized Remote exportObject(Remote obj){
        Remote proxy;
        Integer id = calculateId(obj);
        
        if(!PROXY_BY_ID.containsKey(id)){
            proxy = proxify(obj);
            PROXY_BY_ID.put(id, obj);
        }else{
            proxy = PROXY_BY_ID.get(calculateId(obj));
        }

        startServerIfRequired();
        try{
            server.serve(getRemoteReferenceForRemoteObj(obj));
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
        return proxy;
    }
    
    public synchronized void unexportObject(Remote obj){
        int id = calculateId(obj);
        PROXY_BY_ID.remove(id);
        REMOTE_REFERENCE_BY_ID.remove(id);
        
        shutdownServerIfRequired();
    }
    
    private synchronized void startServerIfRequired(){
        if(server == null){
            server = new VSServer();
        }
    }
    
    private synchronized void shutdownServerIfRequired(){
        if(PROXY_BY_ID.size() == 0){
            server.shutdown();
            server = null;
        }
    }
    
    private Remote proxify(Remote obj) {
        
        Remote proxy;
        
        VSRemoteReference remoteReference = new VSRemoteReference(obj);
        VSInvocationHandler handler = new VSInvocationHandler(remoteReference);

        ClassLoader cl = obj.getClass().getClassLoader();
        Class<?>[] intfs = ReflectionHelper.getAllInterfaces(obj.getClass());
        
        proxy = (Remote) Proxy.newProxyInstance(cl, intfs, handler);
        
        REMOTE_REFERENCE_BY_ID.put(calculateId(obj), remoteReference);

        return proxy;
    }
    
    public VSRemoteReference getRemoteReferenceForRemoteObj(Object obj){
        return REMOTE_REFERENCE_BY_ID.get(calculateId(obj));
    }
    
    public Object invokeMethod(int objectID, String genericMethodString, Object[] args){

        Object result = null;
        Remote obj = PROXY_BY_ID.get(objectID);
        if(args == null){
            args = new Object[0];
        }
        
        if(obj == null){
            return result;
        }
        
        try{
            
            Method method = ReflectionHelper.getMatchingMethod(
                    obj.getClass(), genericMethodString, args
                );
            
            result = method.invoke(obj, args);
            
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}