package com.dobbinsoft.gus.distribution.client.configcenter.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ConfigContentPublicVO {

    private String mpTitle;

    private String wxShareTitle;

    private String wxShareImg;

    private String logo;

    /**
     * 单位是秒 默认15分钟
     */
    private Integer autoCancelTime;

    /**
     * 单位是秒 默认3天
     */
    private Integer autoConfirmTime;

    /**
     * 商家电话
     */
    private String merchantPhone;

    /**
     * 商户微信二维码
     */
    private String merchantWxQr;

    /**
     * 开启0元支付，默认关闭
     */
    private Boolean enableZeroPay;

    /**
     * 开启前端的位置选择 默认开启
     */
    private Boolean enableLocationChoose;

    /**
     * 开启虚拟仓 默认关闭
     */
    private Boolean enableVirtualLocation;

    /**
     * 超范围订单 默认开启
     */
    private Boolean enableOutRangeOrder;

    /**
     * TODO 代码实现
     * false: countCart时展示购物车类商品类型数。 例如 sku1: 3个   sku2: 4个   输出2
     * true(默认): countCart时展示购物车数量数， 例如 sku1: 3个   sku2: 4个   输出7
     */
    private Boolean enableCartSumSku;

    /**
     * 默认: green_theme
     * theme选用的主题颜色
     */
    private String themeColor;

    /**
     * 开启小程序 订单状态同步 默认开启
     */
    private Boolean enableMpOrderSync;

    /**
     * 是否自动标记打包完成，默认关闭
     */
    private Boolean enableAutoPackage;
}
