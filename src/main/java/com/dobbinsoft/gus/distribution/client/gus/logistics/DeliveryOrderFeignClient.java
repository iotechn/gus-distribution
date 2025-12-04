package com.dobbinsoft.gus.distribution.client.gus.logistics;

import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryOrderCreateDTO;
import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryOrderPackCompleteDTO;
import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryOrderVO;
import com.dobbinsoft.gus.distribution.config.OpenFeignConfig;
import com.dobbinsoft.gus.web.vo.R;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "logistics-delivery-order",
        url = "${gus.distribution.logistics-url}",
        path = "/api/delivery-order",
        configuration = OpenFeignConfig.class)
public interface DeliveryOrderFeignClient {

    @PostMapping
    R<DeliveryOrderVO> create(
            @Valid @RequestBody DeliveryOrderCreateDTO createDTO);

    /**
     * 业务系统调用，表示配送单已经打包完成，可以让配送员接单了
     * @return
     */
    @PostMapping("/pack-complete")
    R<Void> packComplete(
            @Valid @RequestBody DeliveryOrderPackCompleteDTO packCompleteDTO);

    @GetMapping("/{deliveryNo}")
    R<DeliveryOrderVO> getDeliveryOrderByDeliveryNo(
            @PathVariable String deliveryNo);


}
