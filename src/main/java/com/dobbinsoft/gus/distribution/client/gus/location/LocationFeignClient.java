package com.dobbinsoft.gus.distribution.client.gus.location;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.location.model.LocationVO;
import com.dobbinsoft.gus.distribution.config.OpenFeignConfig;
import com.dobbinsoft.gus.web.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "location",
        url = "${gus.distribution.location-url}",
        path = "/api/location",
        configuration = OpenFeignConfig.class)
public interface LocationFeignClient {

    R<PageResult<LocationVO>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam LocationVO.Status status,
            @RequestParam LocationVO.Type type);

}
