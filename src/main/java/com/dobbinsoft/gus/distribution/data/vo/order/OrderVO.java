package com.dobbinsoft.gus.distribution.data.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "订单VO")
public class OrderVO {
    
    @Schema(description = "订单ID", example = "order_123456")
    private String id;
    
    @Schema(description = "订单号", example = "O202412011234567890")
    private String orderNo;
    
    @Schema(description = "订单状态", example = "10")
    private Integer status;
    
    @Schema(description = "订单状态描述", example = "未付款")
    private String statusMsg;
    
    @Schema(description = "支付金额", example = "100.00")
    private BigDecimal payAmount;
    
    @Schema(description = "订单总金额", example = "110.00")
    private BigDecimal amount;
    
    @Schema(description = "物流费用", example = "0.00")
    private BigDecimal logisticsAmount;
    
    @Schema(description = "订单备注", example = "请尽快发货")
    private String remark;
    
    @Schema(description = "支付方式", example = "支付宝")
    private String payMethod;
    
    @Schema(description = "支付单号", example = "pay_123456")
    private String payNo;
    
    @Schema(description = "创建时间", example = "2024-12-01T12:00:00Z")
    private ZonedDateTime createTime;
    
    @Schema(description = "发货时间", example = "2024-12-02T10:00:00Z")
    private ZonedDateTime deliveryTime;
    
    @Schema(description = "确认收货时间", example = "2024-12-05T15:00:00Z")
    private ZonedDateTime confirmTime;
    
    @Schema(description = "收货地址信息")
    private OrderAddressVO address;
    
    @Schema(description = "订单商品列表")
    private List<OrderItemVO> orderItems;
    
    @Getter
    @Setter
    @Schema(description = "订单地址VO")
    public static class OrderAddressVO {
        @Schema(description = "收件人姓名", example = "张三")
        private String userName;
        
        @Schema(description = "收件人手机号", example = "13800138000")
        private String telNumber;
        
        @Schema(description = "邮编", example = "100000")
        private String postalCode;
        
        @Schema(description = "省份", example = "北京市")
        private String provinceName;
        
        @Schema(description = "城市", example = "北京市")
        private String cityName;
        
        @Schema(description = "区县", example = "朝阳区")
        private String countyName;
        
        @Schema(description = "详细地址", example = "三里屯街道1号")
        private String detailAddress;
    }
    
    @Getter
    @Setter
    @Schema(description = "订单商品VO")
    public static class OrderItemVO {
        @Schema(description = "订单商品ID", example = "order_item_123")
        private String id;
        
        @Schema(description = "商品名称", example = "iPhone 15")
        private String itemName;
        
        @Schema(description = "SKU名称", example = "iPhone 15 128GB 黑色")
        private String skuName;
        
        @Schema(description = "商品款号", example = "IPHONE15")
        private String smc;
        
        @Schema(description = "SKU", example = "IPHONE15_128GB_BLACK")
        private String sku;
        
        @Schema(description = "分类ID", example = "category_1")
        private String categoryId;
        
        @Schema(description = "分类名称", example = "手机数码")
        private String categoryName;
        
        @Schema(description = "商品单位", example = "台")
        private String unit;
        
        @Schema(description = "商品图片URL", example = "https://example.com/image.jpg")
        private String productPic;
        
        @Schema(description = "商品缩略图URL", example = "https://example.com/thumb.jpg")
        private String productPicSmall;
        
        @Schema(description = "购买数量", example = "1")
        private Integer qty;
        
        @Schema(description = "商品价格", example = "5999.00")
        private BigDecimal price;
        
        @Schema(description = "商品原价", example = "6999.00")
        private BigDecimal originalPrice;
        
        @Schema(description = "商品备注", example = "限时特价")
        private String remark;
    }
}
