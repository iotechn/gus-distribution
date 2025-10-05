package com.dobbinsoft.gus.distribution.data.enums;

import lombok.Getter;

@Getter
public enum RefundStatusType implements BaseEnums<Integer> {
    PENDING(1, "待审核"),
    APPROVED(2, "已同意"),
    REJECTED(3, "已拒绝"),
    PROCESSING(4, "退款处理中"),
    SUCCESS(5, "退款成功"),
    FAILED(6, "退款失败"),
    CANCELLED(7, "已取消");

    RefundStatusType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

    public Integer getCode() {
        return code;
    }

    public static RefundStatusType getStatusByCode(int statusCode) {
        for (RefundStatusType status : values()) {
            if (status.code == statusCode) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断退款状态是否可以审核
     */
    public static boolean canApprove(int statusCode) {
        return statusCode == PENDING.getCode();
    }

    /**
     * 判断退款状态是否可以取消
     */
    public static boolean canCancel(int statusCode) {
        return statusCode == PENDING.getCode() || statusCode == APPROVED.getCode();
    }
}

