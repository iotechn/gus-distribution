package com.dobbinsoft.gus.distribution.client.configcenter.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ConfigContentVO extends ConfigContentPublicVO {

    private Secret secret;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Secret {
        private String wechatMiniAppId;
        private String wechatMiniSecret;
    }


    public ConfigContentPublicVO toPublicVO() {
        ConfigContentPublicVO publicVO = new ConfigContentPublicVO();
        BeanUtils.copyProperties(this, publicVO);
        return publicVO;
    }


}
