package com.dobbinsoft.gus.distribution.controller.open;

import com.dobbinsoft.gus.distribution.data.vo.customizer.CustomizerVO;
import com.dobbinsoft.gus.distribution.service.CustomizerService;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "前台-自定义页面", description = "自定义页面")
@RestController
@RequestMapping("/open/customizer")
@RequiredArgsConstructor
public class OpenCustomizerController {

    private final CustomizerService customizerService;

    @Operation(summary = "获取自定义页面详情", description = "根据ID获取自定义页面详细信息")
    @GetMapping("/{id}")
    public R<CustomizerVO> detail(
            @Parameter(description = "自定义页面ID", required = true, example = "custom_123456")
            @PathVariable String id) {
        CustomizerVO customizerVO = customizerService.detail(id);
        return R.success(customizerVO);
    }

    @Operation(summary = "获取第一个页面", description = "获取租户第一个自定义页面详细信息")
    @GetMapping("/first")
    public R<CustomizerVO> first() {
        CustomizerVO customizerVO = customizerService.first();
        return R.success(customizerVO);
    }

}
