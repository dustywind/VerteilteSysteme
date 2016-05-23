package vsue.rmi;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.HashSet;

public class ReflectionHelper {
    
    public static Class<?>[] getAllInterfaces(Class<?> cl){
        Set<Class<?>> interfaces = new HashSet<Class<?>>();;
        
        for(; cl != null; cl = cl.getSuperclass()){
            for(Class<?> i : cl.getInterfaces()){
                interfaces.add(i);
            }
        }
        
        return interfaces.toArray(new Class<?>[0]);
        
    }
    
    public static Method getMatchingMethod(Class<?> cl, String methodName, Object[] args){
        
        Method match = null;
        for(Method m : cl.getMethods()){
            if(m.getName().compareTo(methodName) == 0){
                Class<?>[] argTypes = m.getParameterTypes();
                // TODO this is not reliable
                if(argTypes.length == args.length){
                    match = m;
                }
            }
        }
        
        return match;
    }
}