package com.dobbinsoft.gus.distribution.client.oauth;

import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.data.vo.user.UserWechatMpLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
public class WechatMpAuthenticator {

    private static final String WECHAT_API_BASE_URL = "https://api.weixin.qq.com";
    private static final String JSCODE2SESSION_PATH = "/sns/jscode2session";

    private final RestTemplate restTemplate;

    public WechatMpAuthenticator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserWechatMpLoginVO authenticate(String appid, String secret, String jsCode, String grantType) {
        try {
            // 构建请求URL
            URI uri = UriComponentsBuilder.fromUriString(WECHAT_API_BASE_URL + JSCODE2SESSION_PATH)
                    .queryParam("appid", appid)
                    .queryParam("secret", secret)
                    .queryParam("js_code", jsCode)
                    .queryParam("grant_type", grantType)
                    .build()
                    .toUri();

            log.info("调用微信API获取用户信息，URL: {}", uri);

            // 发送GET请求
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            log.info("微信API响应状态码: {}, Content-Type: {}, 响应体: {}", 
                    response.getStatusCode(), 
                    response.getHeaders().getContentType(),
                    response.getBody());

            // 检查响应状态
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("微信API调用失败，状态码: {}, 响应体: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("微信API调用失败，状态码: " + response.getStatusCode());
            }

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.error("微信API返回空响应");
                throw new RuntimeException("微信API返回空响应");
            }

            // 使用JsonUtil手动反序列化
            UserWechatMpLoginVO result;
            try {
                result = JsonUtil.convertValue(responseBody, UserWechatMpLoginVO.class);
                if (result == null) {
                    log.error("反序列化结果为空，响应体: {}", responseBody);
                    throw new RuntimeException("反序列化结果为空");
                }
                log.info("微信API响应反序列化成功，openid: {}, errcode: {}, errmsg: {}", 
                        result.getOpenid(), result.getErrcode(), result.getErrmsg());
            } catch (Exception e) {
                log.error("微信API响应反序列化失败，响应体: {}, 错误: {}", responseBody, e.getMessage(), e);
                throw new RuntimeException("微信API响应反序列化失败: " + e.getMessage(), e);
            }

            // 如果返回错误码，记录日志但不抛出异常（由调用方处理）
            if (result.getErrcode() != null && result.getErrcode() != 0) {
                log.warn("微信API返回错误，errcode: {}, errmsg: {}, 响应体: {}", 
                        result.getErrcode(), result.getErrmsg(), responseBody);
            }

            return result;

        } catch (RestClientException e) {
            log.error("调用微信API时发生RestClientException，错误: {}", e.getMessage(), e);
            throw new RuntimeException("调用微信API失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("调用微信API时发生未知异常，错误: {}", e.getMessage(), e);
            throw new RuntimeException("调用微信API失败: " + e.getMessage(), e);
        }
    }
}
