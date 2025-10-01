package sk.mvp.user_service.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
// Used for logging in business layer
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Pointcut pre controller (na kotre metody sa ma aplikovat
    @Pointcut("execution(* sk.mvp.user_service.service..*(..))")
    public void serviceMethods() {}

    // Pred spustením
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("[{}] [SERVICE] Entering method: {} with args: {}",
                correlationId,
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // Po úspešnom návrate
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("[{}] [SERVICE] Exiting method: {} with result: {}",
                correlationId,
                joinPoint.getSignature().toShortString(),
                result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Exception ex) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);

        logger.error("[{}] [SERVICE] Exception in method: {} with args: {}. Exception: {}",
                correlationId,
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()),
                ex.getMessage());
    }

}

