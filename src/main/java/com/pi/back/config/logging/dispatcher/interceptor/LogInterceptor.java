package com.pi.back.config.logging.dispatcher.interceptor;

import com.pi.back.config.logging.dispatcher.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LogInterceptor implements HandlerInterceptor {

    LoggingService loggingService;

    @Autowired
    public LogInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name()) && request.getMethod().equals(HttpMethod.GET.name())) {
            loggingService.logRequest(request, null);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}
