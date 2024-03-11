package com.example.gmall.service.item.mybatis.sql;

import com.example.gmall.service.item.mybatis.annotation.MySQL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MySqlSession {
    public<T> T getMapper(Class<T> dao) { //通常传入一个dao接口 e.g. sqlSession.getMapper(PersonDao.class);
        //返回mapper示例操作数据库
        T instance = (T) Proxy.newProxyInstance(
                dao.getClassLoader(),
                new Class<?>[]{dao} , //？是任何类型，T是单一类型，Java的泛型是在编译时处理的，泛型信息在运行时不可用，这被称为类型擦除。使用?可以避免在运行时处理泛型时的一些复杂性，因为你不需要指定具体的泛型类型
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("获取链接....");

                        final int[] index = {0};
                        Map<String, Object> collect = Arrays
                                .stream(method.getParameters())
                                .collect(Collectors.toMap(item -> item.getName(), item -> args[index[0]++])); //方法的值是要赋给sql参数的
                        //@MySQL("insert into person('age','email') values({age},{email})")
                        //void insertPerson(Integer age, String email);
                        //{'age'=20, 'email'='c@gmail.com'}

                        System.out.println("参数的map.."+collect);

                        for (Parameter parameter : method.getParameters()) {
                            System.out.println(parameter);
                        }

                        //通过反射拿到注解
                        MySQL mySQL = method.getDeclaredAnnotation(MySQL.class);
                        //获取注解上的sql
                        String value = mySQL.value();
                        System.out.println("准备SQL："+value);
                        String sqlType = "";
                        if (value.startsWith("select")) {
                            sqlType = "select";
                        }
                        if( value.startsWith("insert")){
                            sqlType = "insert";
                        }
                        System.out.println("执行SQL；这是一次"+sqlType);
                        if(sqlType.equals("insert")){
                            for (Parameter parameter : method.getParameters()) {
                                String x = "{"+parameter.getName()+"}";
                                value = value.replace(x,collect.get(parameter.getName()).toString()); //把param替换成实际值，从{'age'=20, 'email'='c@gmail.com'} map中拿
                            }
                        }
                        System.out.println("最终需要执行的SQL是："+value);
                        System.out.println("获取返回结果...");
                        Class<?> returnType = method.getReturnType();
                        System.out.println("分析 ResultSet，并封装为:"+returnType);

                        return null;
                    }
                }
        );
        return instance;
    }
}
