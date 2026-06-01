package com.example.orderservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.orderservice.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            log.info("Started: {}.{}", className, methodName);

            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed: {}.{} in {} ms", className, methodName, duration);

            return result;
        } catch (Throwable exception) {
            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "Failed: {}.{} after {} ms. Reason: {}",
                    className,
                    methodName,
                    duration,
                    exception.getMessage()
            );

            throw exception;
        }
    }
}