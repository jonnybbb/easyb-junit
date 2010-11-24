package org.easyb.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.reflect.Proxy.newProxyInstance;


public class VirtualProxy implements InvocationHandler {
    private Object realSubject;
    private final Object[] constrParams;
    private final Constructor<?> subjectConstr;

    private VirtualProxy(Class<?> realSubjectClass, Class[] constrParamTypes, Object[] constrParams) {
        try {
            subjectConstr = realSubjectClass.getConstructor(constrParamTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        this.constrParams = constrParams;
    }

    private Object realSubject() throws Throwable {
        synchronized (this) {
            if (realSubject == null) {
                realSubject = subjectConstr.newInstance(constrParams);
            }
        }
        return realSubject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.flush();
        System.out.println();
        System.out.println("---- before invoke method: "+method.getName());

        Object invoke = method.invoke(realSubject(), args);
        System.out.flush();
        
        System.out.println();

        System.out.println("---- after invoke method: "+method.getName());
        System.out.println();
        return invoke;
    }


    private static VirtualProxy createProxyForClassWithNoArgsConstructor(Class<?> realSubjectClass) {
        return new VirtualProxy(realSubjectClass, new Class[0], new Object[0]);
    }

    private static VirtualProxy createProxyForObject(Class<?> realSubjectClass, Class[] constrParamTypes, Object[] constrParams) {
        return new VirtualProxy(realSubjectClass, constrParamTypes, constrParams);
    }

    @SuppressWarnings("unchecked")
    public static <T, I extends T> T createProxy(Class<T> type, Class<I> implementation, ClassLoader classLoader) {
        return (T) newProxyInstance(classLoader, new Class[]{type}, createProxyForClassWithNoArgsConstructor(implementation));

    }

     public static <T, I extends T> T createProxyForObject(Class<T> type, Class<I> implementation, Class[] constrParamTypes, Object[] constrParams,ClassLoader classLoader) {
        return (T) newProxyInstance(classLoader, new Class[]{type}, createProxyForObject(implementation, constrParamTypes, constrParams));

    }
}