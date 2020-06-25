package com.example.wenda.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger =LoggerFactory.getLogger( LogAspect.class);

    @Before("execution(* com.example.wenda.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinpoint) {
        StringBuilder sb=new StringBuilder();
        for(Object arg:joinpoint.getArgs())
        {
            sb.append("arg"+arg.toString());
        }
        logger.info("before method"+sb.toString());
    }

    @After("execution(* com.example.wenda.controller.*Controller.*(..))")
    public void afterMethod() {
       logger.info("after method"+new Date());
    }
}
