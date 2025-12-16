package com.dobbinsoft.gus.distribution.controller.bo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.erp.ErpProviderCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.erp.ErpProviderUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.erp.ErpProviderPageVO;
import com.dobbinsoft.gus.distribution.data.vo.erp.ErpProviderVO;
import com.dobbinsoft.gus.distribution.service.ErpProviderService;
import com.dobbinsoft.gus.web.vo.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "后台-ERP提供商管理", description = "ERP提供商的查询、创建、更新操作（只允许创建一个）")
@RestController
@RequestMapping("/bo/erp-provider")
@RequiredArgsConstructor
public class BoErpProviderController {

    private final ErpProviderService erpProviderService;

    @Operation(summary = "分页查询ERP提供商", description = "分页查询ERP提供商列表（仅会有一条记录），分页结果不返回config字段")
    @GetMapping
    public R<PageResult<ErpProviderPageVO>> page(
            @Parameter(description = "页码", example = "1")
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageResult<ErpProviderPageVO> result = erpProviderService.page(pageNum, pageSize);
        return R.success(result);
    }

    @Operation(summary = "获取ERP提供商详情", description = "根据ID获取ERP提供商配置信息")
    @GetMapping("/{id}")
    public R<ErpProviderVO> get(
            @Parameter(description = "ERP提供商ID", required = true)
            @PathVariable String id) {
        ErpProviderVO vo = erpProviderService.get(id);
        return R.success(vo);
    }

    @Operation(summary = "创建ERP提供商", description = "创建新的ERP提供商配置（只允许创建一个）")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public R<ErpProviderVO> create(
            @Parameter(description = "ERP提供商创建信息", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "创建ERP提供商", value = """
                        {
                          "type": "JDY",
                          "config": "{\\"key\\":\\"xxx\\",\\"secret\\":\\"xxx\\",\\"clientId\\":\\"xxx\\",\\"clientSecret\\":\\"xxx\\",\\"accountId\\":\\"xxx\\",\\"provider\\":\\"xxx\\",\\"customerCode\\":\\"xxx\\"}",
                          "remark": "简道云ERP配置"
                        }
                        """)
                }))
            @Valid @RequestBody ErpProviderCreateDTO createDTO) {
        ErpProviderVO vo = erpProviderService.create(createDTO);
        return R.success(vo);
    }

    @Operation(summary = "更新ERP提供商", description = "根据ID更新ERP提供商配置信息")
    @PutMapping("/{id}")
    public R<ErpProviderVO> update(
            @Parameter(description = "ERP提供商ID", required = true)
            @PathVariable String id,
            @Parameter(description = "ERP提供商更新信息", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "更新ERP提供商", value = """
                        {
                          "type": "JDY",
                          "config": "{\\"key\\":\\"xxx\\",\\"secret\\":\\"xxx\\"}",
                          "remark": "更新后的备注"
                        }
                        """)
                }))
            @Valid @RequestBody ErpProviderUpdateDTO updateDTO) {
        ErpProviderVO vo = erpProviderService.update(id, updateDTO);
        return R.success(vo);
    }
}
