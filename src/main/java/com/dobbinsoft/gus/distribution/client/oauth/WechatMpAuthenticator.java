package com.dobbinsoft.gus.distribution.client.oauth;

import com.dobbinsoft.gus.distribution.data.vo.user.UserWechatMpLoginVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "wechat-mp-authenticator",
        url = "https://api.weixin.qq.com")
public interface WechatMpAuthenticator {

    @GetMapping("/sns/jscode2session")
    UserWechatMpLoginVO authenticate(
            @RequestParam String appid,
            @RequestParam String secret,
            @RequestParam("js_code") String jsCode,
            @RequestParam("grant_type") String grantType);

}
