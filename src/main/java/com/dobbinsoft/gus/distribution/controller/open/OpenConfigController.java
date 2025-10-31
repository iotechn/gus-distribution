package com.dobbinsoft.gus.distribution.controller.open;

import com.dobbinsoft.gus.distribution.data.vo.config.ConfigContentPublicExtVO;
import com.dobbinsoft.gus.distribution.service.OpenConfigService;
import com.dobbinsoft.gus.web.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/config")
public class OpenConfigController {

    @Autowired
    private OpenConfigService openConfigService;

    @GetMapping
    public R<ConfigContentPublicExtVO> getConfigContentPublic() {
        return R.success(openConfigService.getPublicConfigWithLocations());
    }

}
