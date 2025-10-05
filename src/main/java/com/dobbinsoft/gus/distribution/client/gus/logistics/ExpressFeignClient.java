package com.dobbinsoft.gus.distribution.client.gus.logistics;

import com.dobbinsoft.gus.distribution.config.OpenFeignConfig;
import com.dobbinsoft.gus.distribution.data.enums.LpCode;
import com.dobbinsoft.gus.distribution.data.vo.express.ExpressOrderVO;
import com.dobbinsoft.gus.web.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "logistics-express",
        url = "${gus.distribution.logistics-url}",
        path = "/api/express",
        configuration = OpenFeignConfig.class)
public interface ExpressFeignClient {

    @GetMapping
    public R<ExpressOrderVO> get(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) LpCode lpCode,
            @RequestParam(required = false) String transNo,
            @RequestParam(required = false) String mobile);

}
