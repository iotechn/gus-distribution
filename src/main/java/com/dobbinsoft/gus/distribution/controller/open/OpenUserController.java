package com.dobbinsoft.gus.distribution.controller.open;

import com.dobbinsoft.gus.common.model.constant.HeaderConstants;
import com.dobbinsoft.gus.distribution.data.dto.user.UserWechatMpLoginDTO;
import com.dobbinsoft.gus.distribution.data.vo.user.AuthResultVO;
import com.dobbinsoft.gus.distribution.service.UserService;
import com.dobbinsoft.gus.web.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open/user")
public class OpenUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/wechat-mini-login")
    public R<AuthResultVO> wechatMiniLogin(
            @RequestHeader(HeaderConstants.TENANT_ID_VALUE) String tenantId,
            @RequestBody UserWechatMpLoginDTO userWechatMpLoginDTO) {
        return R.success(userService.wechatMiniLogin(userWechatMpLoginDTO, tenantId));
    }

}
