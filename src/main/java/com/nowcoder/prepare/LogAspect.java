package com.nowcoder.prepare;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
   @Before("execution(* com.nowcoder.prepare.IndexController.*(..))")
   public void beforeMethod(JoinPoint joinPoint){
       StringBuilder sb = new StringBuilder();
       for(Object args : joinPoint.getArgs()){
           sb.append("args"+args.toString()+"|");
       }
     log.info("before method");
   }
   @After("execution(* com.nowcoder.prepare.IndexController.*(..))")
   public void afterMethod(){
     log.info("after method");
   }
}
