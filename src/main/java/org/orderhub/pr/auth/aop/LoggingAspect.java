package org.orderhub.pr.auth.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @After("execution(* org.orderhub.pr.auth.service.*.update*(..))")
    public void logUpdateMethods(JoinPoint joinPoint) {
        if (joinPoint.getSignature().getName().startsWith("update")) {
            logger.info("Executed method: {}", joinPoint.getSignature());
            logger.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));
        }
    }

}