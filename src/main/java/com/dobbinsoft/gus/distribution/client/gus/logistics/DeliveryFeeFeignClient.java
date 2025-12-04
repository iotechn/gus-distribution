package com.dobbinsoft.gus.distribution.client.gus.logistics;

import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryFeePreviewDTO;
import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryFeePreviewVO;
import com.dobbinsoft.gus.distribution.config.OpenFeignConfig;
import com.dobbinsoft.gus.web.vo.R;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "logistics-delivery-fee",
        url = "${gus.distribution.logistics-url}",
        path = "/api/delivery-fee",
        configuration = OpenFeignConfig.class)
public interface DeliveryFeeFeignClient {

    @PostMapping("/preview")
    R<DeliveryFeePreviewVO> previewDeliveryFee(@Valid @RequestBody DeliveryFeePreviewDTO previewDTO);

}
