package com.dobbinsoft.gus.distribution.interceptor;

import java.util.Optional;

import org.springframework.web.servlet.HandlerInterceptor;

import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.IdentityContext;
import com.dobbinsoft.gus.distribution.data.dto.session.BoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        BoSessionInfoDTO boSessionInfoDTO = new BoSessionInfoDTO();
        Optional<IdentityContext> identityContextOptional = GenericRequestContextHolder.getIdentityContext();
        if (identityContextOptional.isPresent()) {
            IdentityContext identityContext = identityContextOptional.get();
            boSessionInfoDTO.setUserId(identityContext.getUserId());
            boSessionInfoDTO.setEmployeeEmail(identityContext.getEmployeeEmail());
            // 可对BO请求进行拦截
            SessionUtils.putBoSession(boSessionInfoDTO);
            return true;
        } else {
            throw new ServiceException(BasicErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // clear
        SessionUtils.removeBoSession();
    }
}
