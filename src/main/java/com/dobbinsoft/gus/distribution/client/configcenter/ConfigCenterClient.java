package com.dobbinsoft.gus.distribution.client.configcenter;


import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentVO;

public interface ConfigCenterClient {

    void save(ConfigContentVO configContentVO);

    ConfigContentVO getBrandAllConfigContent();

}
