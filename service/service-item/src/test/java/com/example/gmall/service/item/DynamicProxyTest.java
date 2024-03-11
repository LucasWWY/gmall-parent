package com.example.gmall.service.item;

import lombok.Data;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 9/3/2024 - 4:05 pm
 * @Description
 */

public class DynamicProxyTest {
    public static void main(String[] args) {
        Audi audi1 = new Audi();
        Audi audi2 = new Audi();
        Audi audi3 = new Audi();
        audi1.setName("Audi1");
        audi2.setName("Audi2");
        audi3.setName("Audi3");
        audi1.move();
        audi2.move();
        audi3.move();

        //每一辆车行驶之前都需要 加油 启动引擎 之后需要 停车，
        //这些就是公共逻辑可以抽取出来用AOP

        //动态代理v1.0：JDK动态代理，必须接口
        //Car proxy = (Car) Proxy.newProxyInstance(audi1.getClass().getClassLoader(), audi1.getClass().getInterfaces(),
        Car proxy = (Car) Proxy.newProxyInstance(Audi.class.getClassLoader(), Audi.class.getInterfaces(),
                new InvocationHandler() {
                    /**
                     * Processes a method invocation on a proxy instance and returns
                     * @param proxy the proxy instance that the method was invoked on
                     * 经纪人
                     *
                     * @param method the {@code Method} instance corresponding to
                     * the interface method invoked on the proxy instance.  The declaring
                     * class of the {@code Method} object will be the interface that
                     * the method was declared in, which may be a superinterface of the
                     * proxy interface that the proxy class inherits the method through.
                     *
                     * @param args an array of objects containing the values of the
                     * arguments passed in the method invocation on the proxy instance,
                     * or {@code null} if interface method takes no arguments.
                     * Arguments of primitive types are wrapped in instances of the
                     * appropriate primitive wrapper class, such as
                     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
                     *
                     * @return
                     * @throws Throwable
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //1. 利用反射调用目标对象的方法
                        System.out.println("代理正在执行..." + this);

                        Object invoke = null;
                        //1、目标方法执行之前： 前置通知
                        try {
                            //2、目标方法正常执行完成： 返回通知
                            invoke = method.invoke(audi1, args); //这里的audi1是目标对象，不能写this，this在此表示的是代理对象
                            //Object invoke = method.invoke(audi2, args);
                            //Object invoke = method.invoke(audi3, args);
                        } catch (Exception e) {
                            //3、目标方法异常完成： 异常通知
                        } finally {
                            //4、最终都要做的事情：后置通知
                        }



                        return invoke; //当proxy的方法被调用时，实际调用了被代理对象的方法，invode就是被代理对象的方法的返回值
                    }
                });

        proxy.move(); //本质上是通过了method.invoke(audi1, args);调用了audi1.move()

        //动态代理v2.0：cglib 不需要实现任何接口，只需要目标类的子类
        BMW bmw = new BMW();
        bmw.move();

        //1. 创建一个增强器
        Enhancer enhancer = new Enhancer();
        //2. 设置父类
        enhancer.setSuperclass(BMW.class);
        //3. 设置回调
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * 调用每个方法都会进入拦截器；
             * @param o 创建出的代理对象proxy2
             * @param method
             * @param args 参数 i.e. args
             * @param methodProxy
             * @return
             * @throws Throwable
             */
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                //System.out.println(o);
                //System.out.println(method);
                //注意因为o就是我们创建出的代理对象proxy2，System.out.println(o); 实际上在调用o.toString() -> proxy2.toString()，而对代理对象的每一次方法调用都会被intercept拦截，
                //所以这里的sout会导致无限递归调用，栈溢出
                System.out.println(method);

                //代理bmw对象，执行目标对象方法
                //方法一：
                //method.invoke(bmw, args);
                //方法二：
                Object object = methodProxy.invokeSuper(o, args);//找到o (i.e. proxy2) 的父类，调用父类的方法
//methodProxy.invoke() 会调用proxy2的方法，被拦截，导致无限递归调用，栈溢出

                return object;
            }
        });

        //4. 创建代理对象
        BMW proxy2 = (BMW) enhancer.create();
        proxy2.move();
    }

}

interface Car {
    void move();
}

@Data
class Audi implements Car {

    private String name;

    @Override
    public void move() {
        System.out.println(name + " is moving...");
    }
}

class BMW {

    private String name;

    public void move() {
        System.out.println(name + " is moving...");
    }
}
