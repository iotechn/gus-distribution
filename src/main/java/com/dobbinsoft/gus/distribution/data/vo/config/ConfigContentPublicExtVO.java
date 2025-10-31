package com.dobbinsoft.gus.distribution.data.vo.config;

import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentPublicVO;
import com.dobbinsoft.gus.distribution.client.gus.location.model.LocationVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfigContentPublicExtVO extends ConfigContentPublicVO {

    private List<LocationVO> locations;

}
