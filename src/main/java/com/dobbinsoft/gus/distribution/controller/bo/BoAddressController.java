package com.dobbinsoft.gus.distribution.controller.bo;

import com.dobbinsoft.gus.web.vo.R;
import com.dobbinsoft.gus.distribution.data.dto.address.AddressQueryDTO;
import com.dobbinsoft.gus.distribution.data.vo.address.AddressVO;
import com.dobbinsoft.gus.distribution.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理后台-地址管理", description = "管理员查看用户地址信息")
@RestController
@RequestMapping("/bo/addresses")
public class BoAddressController {

    @Autowired
    private AddressService addressService;

    @Operation(summary = "获取地址详情", description = "根据ID获取地址详细信息（管理员权限）")
    @GetMapping("/{id}")
    public R<AddressVO> getById(
            @Parameter(description = "地址ID", required = true, example = "addr_123456")
            @PathVariable String id) {
        AddressVO addressVO = addressService.getById(id);
        return R.success(addressVO);
    }

    @Operation(summary = "查询地址列表", description = "根据条件查询地址列表，支持按用户、姓名、手机号、地区等过滤（管理员权限）")
    @GetMapping
    public R<List<AddressVO>> query(
            @Parameter(description = "用户ID", example = "user_123456")
            @RequestParam(required = false) String userId,
            @Parameter(description = "收件人姓名，支持模糊查询", example = "张三")
            @RequestParam(required = false) String userName,
            @Parameter(description = "收件人手机号", example = "13800138000")
            @RequestParam(required = false) String telNumber,
            @Parameter(description = "省份", example = "北京市")
            @RequestParam(required = false) String provinceName,
            @Parameter(description = "城市", example = "北京市")
            @RequestParam(required = false) String cityName,
            @Parameter(description = "县/区", example = "朝阳区")
            @RequestParam(required = false) String countyName,
            @Parameter(description = "是否默认地址", example = "true")
            @RequestParam(required = false) Boolean isDefault,
            @Parameter(description = "地址标签", example = "家", 
                schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"家", "公司", "学校", "其他"}))
            @RequestParam(required = false) String label) {
        AddressQueryDTO queryDTO = new AddressQueryDTO();
        queryDTO.setUserId(userId);
        queryDTO.setUserName(userName);
        queryDTO.setTelNumber(telNumber);
        queryDTO.setProvinceName(provinceName);
        queryDTO.setCityName(cityName);
        queryDTO.setCountyName(countyName);
        queryDTO.setIsDefault(isDefault);
        queryDTO.setLabel(label);
        
        List<AddressVO> addressVOList = addressService.query(queryDTO);
        return R.success(addressVOList);
    }
} 