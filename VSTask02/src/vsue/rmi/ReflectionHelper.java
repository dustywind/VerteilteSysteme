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
                    boolean paramMatch = true;
                    for(int i = 0; i < args.length; ++i){
                        
                        Class<?> argClass = argTypes[i];
                        Object obj = args[i];
                        
                        paramMatch &= instanceIsAssignable(obj, argClass);
                    }
                    if(paramMatch){
                        match = m;
                    }
                }
            }
        }
        
        return match;
    }
    
    public static boolean instanceIsAssignable(Object obj, Class<?> cl){
        
        boolean primitiveType = cl.isPrimitive();
        boolean nullInstance = obj == null;
        
        if(nullInstance){
            return !primitiveType;
        }
        
        if(primitiveType){
            if(obj instanceof Number){
                switch(cl.toString()){
                    case "byte":
                        return obj instanceof Byte;
                    case "double":
                        return obj instanceof Double;
                    case "float":
                        return obj instanceof Float;
                    case "int":
                        return obj instanceof Integer;
                    case "long":
                        return obj instanceof Long;
                    case "short":
                        return obj instanceof Short;
                   default:
                       return false;
                }
            }
            else{
                return false;
            }
        }
        else{
            return cl.isAssignableFrom(obj.getClass());
        }
    }
}