package com.wipro.wega.fraud_transaction_adapter.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Implements the "annotated logging" guideline: methods marked {@link Loggable}
 * get uniform ENTRY/EXIT markers without hand-written log statements. The
 * method name is also placed on the MDC so every line emitted inside the method
 * (and the markers themselves) carries it, alongside the UTC timestamp and
 * level supplied by the logback pattern.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final String MDC_METHOD = "method";

    @Around("@annotation(Loggable)")
    public Object logJourneyStep(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());

        String previousMethod = MDC.get(MDC_METHOD);
        MDC.put(MDC_METHOD, methodName);
        long startNanos = System.nanoTime();
        logger.info("ENTRY {}", methodName);
        try {
            Object result = joinPoint.proceed();
            logger.info("EXIT {} ({} ms)", methodName, elapsedMillis(startNanos));
            return result;
        } catch (Throwable ex) {
            logger.error("EXIT {} failed after {} ms: {}", methodName, elapsedMillis(startNanos),
                    ex.getMessage(), ex);
            throw ex;
        } finally {
            restore(previousMethod);
        }
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private void restore(String previousMethod) {
        if (previousMethod != null) {
            MDC.put(MDC_METHOD, previousMethod);
        } else {
            MDC.remove(MDC_METHOD);
        }
    }
}
