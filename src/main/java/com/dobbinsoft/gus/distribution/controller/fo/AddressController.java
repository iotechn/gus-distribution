package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.web.vo.R;
import com.dobbinsoft.gus.distribution.data.dto.address.AddressQueryDTO;
import com.dobbinsoft.gus.distribution.data.dto.address.AddressUpsertDTO;
import com.dobbinsoft.gus.distribution.data.vo.address.AddressVO;
import com.dobbinsoft.gus.distribution.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "前台-地址管理", description = "用户地址薄管理接口")
@RestController
@RequestMapping("/fo/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Operation(summary = "创建地址", description = "创建新的收货地址")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public R<AddressVO> create(
            @Parameter(description = "地址创建信息", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "创建家庭地址", value = """
                        {
                          "userName": "张三",
                          "telNumber": "13800138000",
                          "postalCode": "100000",
                          "provinceName": "北京市",
                          "cityName": "北京市",
                          "countyName": "朝阳区",
                          "detailAddress": "三里屯街道1号",
                          "isDefault": true,
                          "label": "家"
                        }
                        """),
                    @ExampleObject(name = "创建公司地址", value = """
                        {
                          "userName": "李四",
                          "telNumber": "13900139000",
                          "postalCode": "200000",
                          "provinceName": "上海市",
                          "cityName": "上海市",
                          "countyName": "浦东新区",
                          "detailAddress": "陆家嘴金融中心2号",
                          "isDefault": false,
                          "label": "公司"
                        }
                        """)
                }))
            @Valid @RequestBody AddressUpsertDTO upsertDTO) {
        AddressVO addressVO = addressService.create(upsertDTO);
        return R.success(addressVO);
    }

    @Operation(summary = "更新地址", description = "根据ID更新地址信息")
    @PutMapping("/{id}")
    public R<AddressVO> update(
            @Parameter(description = "地址ID", required = true, example = "addr_123456")
            @PathVariable String id,
            @Parameter(description = "地址更新信息", required = true)
            @Valid @RequestBody AddressUpsertDTO upsertDTO) {
        AddressVO addressVO = addressService.update(id, upsertDTO);
        return R.success(addressVO);
    }

    @Operation(summary = "删除地址", description = "删除指定的收货地址")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public R<Void> delete(
            @Parameter(description = "地址ID", required = true, example = "addr_123456")
            @PathVariable String id) {
        addressService.delete(id);
        return R.success();
    }

    @Operation(summary = "获取地址详情", description = "根据ID获取地址详细信息")
    @GetMapping("/{id}")
    public R<AddressVO> getById(
            @Parameter(description = "地址ID", required = true, example = "addr_123456")
            @PathVariable String id) {
        AddressVO addressVO = addressService.getById(id);
        return R.success(addressVO);
    }

    @Operation(summary = "查询地址列表", description = "根据条件查询用户地址列表，支持按姓名、手机号、地区等过滤")
    @GetMapping
    public R<List<AddressVO>> query(
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

    @Operation(summary = "获取默认地址", description = "获取用户的默认收货地址")
    @GetMapping("/default")
    public R<AddressVO> getDefaultAddress() {
        AddressVO addressVO = addressService.getDefaultAddress();
        return R.success(addressVO);
    }


} 