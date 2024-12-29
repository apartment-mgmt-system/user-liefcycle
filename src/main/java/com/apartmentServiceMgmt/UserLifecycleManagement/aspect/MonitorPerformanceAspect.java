package com.apartmentServiceMgmt.UserLifecycleManagement.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
/** Aspect class to check performance of methods annotated using MonitorPerformance
 *  
 */
public class MonitorPerformanceAspect {
	 @Around("@annotation(com.apartmentServiceMgmt.UserLifecycleManagement.service.MonitorPerformance)")
	    public Object monitorExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
	        long startTime = System.currentTimeMillis();
	        
	        Object proceed = joinPoint.proceed();
	        
	        long executionTime = System.currentTimeMillis() - startTime;
	        
	        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");
	        
	        return proceed;
	    }}
