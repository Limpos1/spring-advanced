package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AoP {
    private static final Logger logger = LoggerFactory.getLogger(AoP.class);

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))" +
            "||execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public Object logApiRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String userId = request.getParameter("userId");
            String requestUrl = request.getRequestURL().toString();
            LocalDateTime requestTime = LocalDateTime.now();

            logger.info("API 요청 정보 : [User Id: {}, Request URL: {}, Request Time: {}]",userId,requestUrl,requestTime);
        }
        return joinPoint.proceed();
    }

}
