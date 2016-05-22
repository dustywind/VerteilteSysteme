package vsue.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VSRemoteObjectManager{
    
    private static final VSRemoteObjectManager singleton = new VSRemoteObjectManager();

    public static final int DEFAULT_PORT = 0;

    
    
    private final ExportedObjectStorage STORAGE = new ExportedObjectStorage();
    
    public static VSRemoteObjectManager getInstance(){
        return singleton;
    }
    
    public Remote exportObject(Remote obj) {
        return exportObject(obj, DEFAULT_PORT);
    }
    
    public Remote exportObject(Remote obj, int port){
        Remote proxy;
        
        if(!STORAGE.containsObject(obj)){
            proxy = proxify(obj, port);
            STORAGE.insert(obj, obj);
        }else{
            proxy = STORAGE.getById(ExportedObjectStorage.objectToId(obj));
        }

        return proxy;
    }
    
    private Remote proxify(Remote obj, int port) {
        
        Remote proxy;
        
        VSRemoteReference remoteReference = new VSRemoteReference(obj, port);
        VSInvocationHandler handler = new VSInvocationHandler(remoteReference);

        //ClassLoader cl = Remote.class.getClassLoader();
        ClassLoader cl = obj.getClass().getClassLoader();
        Class<?>[] intfs = ReflectionHelper.getAllInterfaces(obj.getClass());
        
        
        proxy = (Remote) Proxy.newProxyInstance(cl, intfs, handler);

        return proxy;
    }
    
    public Object invokeMethod(int objectID, String genericMethodName, Object[] args){

        Object result = null;
        Remote obj = STORAGE.getById(objectID);
        if(args == null){
            args = new Object[0];
        }
        
        if(obj == null){
            return result;
        }
        
        try{
            Class<?>[] paramTypes = new Class<?>[args.length];
            for(int i = 0; i < args.length; ++i){
                paramTypes[i] = args[i].getClass();
            }
            
            Method method = obj.getClass().getMethod(genericMethodName, paramTypes);
            
            result = method.invoke(obj, args);
            
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static class ReflectionHelper{
        
        public static Class<?>[] getAllInterfaces(Class<?> c){
            List<Class<?>> interfaces = new LinkedList<Class<?>>();
            
            for(Class<?> current = c; current != null; current = current.getSuperclass()){
                for(Class<?> n : current.getInterfaces()){
                    if(!interfaces.contains(n)){
                        interfaces.add(n);
                    }
                }
            }
            return interfaces.toArray(new Class<?>[0]);
        }
    }
    
    private static class ExportedObjectStorage{
        private final HashMap<Integer, Remote> STORAGE = new HashMap<Integer, Remote>();

        public static int objectToId(Remote obj){
            return obj.hashCode();
        }
        
        public void insert(int objId, Remote obj){
            STORAGE.put(objId, obj);
        }
        
        public void insert(Remote obj, Remote proxy){
            insert(objectToId(obj), proxy);
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