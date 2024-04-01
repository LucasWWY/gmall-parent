package com.example.gmall.service.item;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 12/3/2024 - 8:22 pm
 * @Description
 */
public class ExpressionTest {

    @Test
    public void test() {
        //1、创建一个表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        //2、解析一个表达式；定界符  //sku:info:#{#args[1]}
        //Expression expression = parser.parseExpression("1+1");
        //Expression expression = parser.parseExpression("'abc'.toUpperCase()"); //能在表达式中用Java函数
        //Expression expression = parser.parseExpression("new com.example.gmall.service.item.ExpressionTest.Person().haha()"); //得到obj方法的返回值
        Expression expression = parser.parseExpression("T(java.util.UUID).randomUUID().toString()");

        //3、得到值
        Object value = expression.getValue();
        System.out.println(value);
    }

    @Test
    public void test2() {
        //1、创建一个表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        //2、ParserContext参数：定界符，解析一个表达式从哪开始解析，其余当作默认字符串；
        //prefix = "#{", suffix = "}"
        //Expression expression = parser.parseExpression("#{1+1}", ParserContext.TEMPLATE_EXPRESSION);
        Expression expression = parser.parseExpression("sku:info:#{#args[0]}", ParserContext.TEMPLATE_EXPRESSION);

        //3、得到值
        EvaluationContext ec = new StandardEvaluationContext();
        ec.setVariable("args",new Long[]{88L,77L,45L});
        //ec.setVariable("sms",new SmsService());
        String value = expression.getValue(ec, String.class); //2nd 期望返回类型
        System.out.println(value);
    }

    public static class Person {
        public String haha(){
            System.out.println("hahaha....");
            return "666";
        }
    }
}
