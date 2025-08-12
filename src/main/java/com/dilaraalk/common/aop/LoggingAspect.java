package com.dilaraalk.common.aop;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Service katmanındaki tüm public metotlar
    @Pointcut("execution(public * com.dilaraalk.user.service..*(..)) || execution(public * com.dilaraalk.product.service..*(..))")
    private void serviceMethods() {}

    // Metot çalışmadan önce log
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.debug(">> {}() çağrıldı. Parametreler: {}", methodName, Arrays.toString(args));
    }

    // Metot başarılı şekilde döndüğünde log
    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("<< {}() tamamlandı. Dönüş değeri: {}", methodName, result);
    }
}