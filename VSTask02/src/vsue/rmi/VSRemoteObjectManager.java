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
            Method method = ReflectionHelper.getMatchingMethod(
                    obj.getClass(), genericMethodName, args
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
        
        public static Method getMatchingMethod(Class<?> cl, String methodName, Object[] params){
            Method method = null;
            Class<?>[] paramTypes = null;
            
            // TODO make better
            for(Method m : cl.getMethods()){
                if(methodName.compareTo(m.getName()) == 0
                    && m.getParameterTypes().length == params.length){
                    method = m;
                }
            }
            
            /*
            for(Method m : cl.getMethods()){
                if(genericMethodName.compareTo(m.toGenericString()) == 0){
                    method = m;
                }
            }
            */
            
            /*
            if(params != null){
                paramTypes = new Class<?>[params.length];
                for(int i = 0; i < params.length; ++i){
                    if(params[i] != null){
                        paramTypes[i] = params[i].getClass();
                    }
                    else{
                        paramTypes[i] = null;
                    }
                }
            }*/
            /*
            for(Method m : cl.getMethods()){
                if(methodName.compareTo(m.getName()) == 0){
                    boolean match = true;
                    Class<?>[] types = m.getParameterTypes();
                    if(types.length != params.length){
                        continue;
                    }
                    for(int i = 0; i < params.length; ++i){
                        if(params[i] != null){
                            if(params[i].getClass() != types[i]){
                                match = false;
                            }
                        }
                    }
                    if(match){
                        method = m;
                    }
                }
            }
            */
            
            return method;

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