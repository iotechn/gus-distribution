package com.dobbinsoft.gus.distribution.client.configcenter.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ConfigContentVO extends ConfigContentPublicVO {

    public ConfigContentPublicVO toPublicVO() {
        ConfigContentPublicVO publicVO = new ConfigContentPublicVO();
        BeanUtils.copyProperties(this, publicVO);
        return publicVO;
    }


}
