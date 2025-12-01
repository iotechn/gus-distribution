package com.dobbinsoft.gus.distribution.interceptor;

import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.IdentityContext;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.data.enums.UserSrcType;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

public class FoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<IdentityContext> identityContextOptional = GenericRequestContextHolder.getIdentityContext();
        if (identityContextOptional.isEmpty()) {
            throw new ServiceException(BasicErrorCode.UNAUTHORIZED);
        }
        IdentityContext identityContext = identityContextOptional.get();
        UserSrcType[] values = UserSrcType.values();
        for (UserSrcType value : values) {
            if (value.name().equals(identityContext.getUserSrc())) {
                FoSessionInfoDTO foSessionInfoDTO = new FoSessionInfoDTO();
                foSessionInfoDTO.setSrc(identityContext.getUserSrc());
                foSessionInfoDTO.setUserId(identityContext.getUserId());
                SessionUtils.putFoSession(foSessionInfoDTO);
                return true;
            }
        }
        throw new ServiceException(BasicErrorCode.UNAUTHORIZED);
    }
}
