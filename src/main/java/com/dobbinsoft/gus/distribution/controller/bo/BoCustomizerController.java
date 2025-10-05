package com.dobbinsoft.gus.distribution.controller.bo;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.customizer.CustomizerVO;
import com.dobbinsoft.gus.distribution.service.CustomizerService;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-自定义页面", description = "自定义页面的增删改查操作")
@RestController
@RequestMapping("/bo/customizer")
@RequiredArgsConstructor
public class BoCustomizerController {

    private final CustomizerService customizerService;

    @Operation(summary = "分页查询自定义页面", description = "根据条件分页查询自定义页面列表")
    @GetMapping
    public R<PageResult<CustomizerVO>> page(
            @Parameter(description = "查询条件", required = true)
            @Valid CustomizerSearchDTO searchDTO) {
        PageResult<CustomizerVO> result = customizerService.page(searchDTO);
        return R.success(result);
    }

    @Operation(summary = "获取自定义页面详情", description = "根据ID获取自定义页面详细信息")
    @GetMapping("/{id}")
    public R<CustomizerVO> detail(
            @Parameter(description = "自定义页面ID", required = true, example = "customizer_123456")
            @PathVariable String id) {
        CustomizerVO customizerVO = customizerService.detail(id);
        return R.success(customizerVO);
    }

    @Operation(summary = "创建自定义页面", description = "创建新的自定义页面")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public R<CustomizerVO> create(
            @Parameter(description = "自定义页面创建信息", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "创建自定义页面", value = """
                        {
                          "name": "首页自定义",
                          "status": 1,
                          "content": "{\\"components\\": []}"
                        }
                        """)
                }))
            @Valid @RequestBody CustomizerCreateDTO createDTO) {
        CustomizerVO customizerVO = customizerService.create(createDTO);
        return R.success(customizerVO);
    }

    @Operation(summary = "更新自定义页面", description = "根据ID更新自定义页面信息")
    @PutMapping("/{id}")
    public R<CustomizerVO> edit(
            @Parameter(description = "自定义页面ID", required = true, example = "customizer_123456")
            @PathVariable String id,
            @Parameter(description = "自定义页面更新信息", required = true)
            @Valid @RequestBody CustomizerUpdateDTO updateDTO) {
        CustomizerVO customizerVO = customizerService.update(id, updateDTO);
        return R.success(customizerVO);
    }

    @Operation(summary = "删除自定义页面", description = "根据ID删除自定义页面")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public R<Void> delete(
            @Parameter(description = "自定义页面ID", required = true, example = "customizer_123456")
            @PathVariable String id) {
        customizerService.delete(id);
        return R.success();
    }


}
