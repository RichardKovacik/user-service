package sk.mvp.user_service.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
// Used for logging in business layer
public class LoggingAspect {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Pointcut pre controller
    @Pointcut("execution(* sk.mvp.user_service.controller..*(..))")
    public void controllerMethods() {}

    // ---------------- Controller ----------------

//    @Before("controllerMethods()")
//    public void logControllerEntry(JoinPoint joinPoint) {
//        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
//        logger.info("[CONTROLLER] Incoming request: {} with input payload: {}",
//                joinPoint.getSignature().toShortString(),
//                Arrays.toString(joinPoint.getArgs()));
//    }

//    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
//    public void logControllerException(JoinPoint joinPoint, Exception ex) {
//        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
//        logger.error("[CONTROLLER] Exception in request: {} with args: {}. Exception: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), ex.toString());
//    }
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Exception ex) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        logger.error("[CONTROLLER] [{}] Exception in method: {} with args: {}. Exception: {}",
                correlationId,
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()),
                ex.getMessage());
    }

}

