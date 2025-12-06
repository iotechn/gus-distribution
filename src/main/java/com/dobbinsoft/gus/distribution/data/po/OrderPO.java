package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@TableName("ds_order")
@Schema(description = "订单实体")
public class OrderPO extends BasePO {

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * com.dobbinsoft.gus.distribution.data.enums.OrderStatusType
     */
    @Schema(description = "订单状态", example = "1")
    @NotNull(message = "订单状态不能为空")
    private Integer status;

    @Schema(description = "配送单号")
    private String deliveryNo;

    @Schema(description = "配送单创建时间")
    private ZonedDateTime deliveryTime;

    @Schema(description = "实际收货日期")
    private ZonedDateTime confirmTime;

    /**
     * 存储在数据库中使用AES进行加密解密
     */
    @Schema(description = "收货地址")
    @NotNull(message = "收货地址不能为空")
    private Address address;

    @Schema(description = "支付方式（如支付宝、微信支付等）")
    private String payMethod;

    @Schema(description = "支付金额")
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payAmount;

    @Schema(description = "订单支付时间")
    private ZonedDateTime payTime;

    @Schema(description = "订单总金额")
    @NotNull(message = "订单总金额不能为空")
    private BigDecimal amount;

    @Schema(description = "支付单号")
    private String payNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "内部备注")
    private String innerRemark;

    @Schema(description = "搜索关键字（冗余字段，用于搜索商品名、SMC、SKU等信息）")
    private String searchKeyword;

    @Schema(description = "下单门店编码（locationCode）")
    private String locationCode;

    @Getter
    @Setter
    @Schema(description = "收货地址信息")
    public static class Address {

        @Schema(description = "用户姓名")
        @NotBlank(message = "用户姓名不能为空")
        private String userName;

        @Schema(description = "邮编")
        private String postalCode;

        @Schema(description = "省份")
        @NotBlank(message = "省份不能为空")
        private String provinceName;

        @Schema(description = "城市")
        @NotBlank(message = "城市不能为空")
        private String cityName;

        @Schema(description = "县")
        @NotBlank(message = "县不能为空")
        private String countyName;

        @Schema(description = "收件人手机号")
        @NotBlank(message = "收件人手机号不能为空")
        private String telNumber;
        
        @Schema(description = "详细地址")
        @NotBlank(message = "详细地址不能为空")
        private String detailAddress;

        @Schema(description = "纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "39.9142")
        @NotNull(message = "纬度不能为空")
        private BigDecimal latitude;
    
        @Schema(description = "经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "116.4174")
        @NotNull(message = "经度不能为空")
        private BigDecimal longitude;
    }

}
