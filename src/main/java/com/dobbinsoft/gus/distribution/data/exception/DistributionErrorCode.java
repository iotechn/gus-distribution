package com.dobbinsoft.gus.distribution.data.exception;

import com.dobbinsoft.gus.web.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum DistributionErrorCode implements ErrorCode {

    // User/Auth related error codes
    AUTHENTICATOR_NOT_EXIST("1001", "不存在该类型的认证器"),

    // Cart related error codes
    CART_SMC_NUMBER_GREATER_THAN_MAX("2001", "购物车商品数量已达上限，最多可添加%d件商品"),
    CART_ITEM_QUANTITY_INVALID("2002", "商品数量必须大于0"),
    
    // Product related error codes
    ITEM_NOT_FOUND("3001", "item not found"),
    INVALID_SKU("3002", "invalid sku"),


    // Order related error codes
    ORDER_ITEMS_EMPTY("4501", "order items cannot be empty"),
    ORDER_SKU_COUNT_EXCEEDED("4502", "single order SKU count cannot exceed 100"),
    ORDER_MIXED_STOCK_TYPES("4503", "stockable and non-stockable items cannot be mixed in one order"),
    ORDER_NOT_FOUND("4504", "order not found"),
    ORDER_STATUS_INVALID("4505", "order status invalid for comment"),
    ORDER_NOT_BELONG_TO_USER("4506", "order does not belong to user"),

    // Refund related error codes
    REFUND_ALREADY_EXISTS("4701", "refund already exists for this order item"),
    REFUND_AMOUNT_EXCEEDED("4702", "refund amount cannot exceed item amount"),
    REFUND_STATUS_INVALID("4703", "refund status invalid for this operation"),
    REFUND_NOT_FOUND("4704", "refund not found"),
    REFUND_NOT_BELONG_TO_USER("4705", "refund does not belong to user"),

    // Comment related error codes
    COMMENT_ORDER_ITEM_NOT_FOUND("4601", "order item not found for comment"),
    COMMENT_PARAM_INVALID("4602", "comment parameter invalid"),
    COMMENT_ALREADY_EXISTS("4603", "comment already exists for this order item"),

    // Social Authenticator related error codes
    SOCIAL_AUTHENTICATOR_NOT_FOUND("5501", "social authenticator not found"),
    SOCIAL_AUTHENTICATOR_ALREADY_EXISTS("5502", "social authenticator already exists for this source type"),


    // System related error codes
    ACCESS_DENIED("7001", "access denied"),
    STOCK_ALREADY_EXISTS("7002", "stock record already exists"),

    // ERP Provider related error codes
    ERP_PROVIDER_ALREADY_EXISTS("8001", "ERP提供商已存在，只允许创建一个"),

    //
    ;
    /**
     * error code
     */
    private final String code;
    /**
     * error message
     */
    private final String message;


    DistributionErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    static DistributionErrorCode getByCode(String code) {
        for (DistributionErrorCode distributionErrorCode : DistributionErrorCode.values()) {
            if (distributionErrorCode.getCode().equals(code)) {
                return distributionErrorCode;
            }
        }
        return null;
    }
}
