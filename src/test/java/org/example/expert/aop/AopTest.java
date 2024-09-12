package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AopTest {

    @InjectMocks
    private AoP aop;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes requestAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }
    @Test
    public void testLogApiRequests() throws Throwable {
        // HttpServletRequest 모킹
        // 로그에 필요한 데이터를 직접 넣어준다.
        when(requestAttributes.getRequest()).thenReturn(request);
        //userId를 주입시킨다.
        when(request.getParameter("userId")).thenReturn("1234");

        // URL은 StringBuffer타입이다.
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));

        // ProceedingJoinPoint 모킹
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        // Aspect 실행
        Object result = aop.logApiRequests(proceedingJoinPoint);

        // 메서드 호출 확인
        verify(request).getParameter("userId");
        verify(request).getRequestURL();
        verify(proceedingJoinPoint).proceed();

        // 리턴 값 검증
        assertEquals("result", result);
    }
}

