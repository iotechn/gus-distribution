package com.dobbinsoft.gus.distribution.data.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum OrderStatusType implements BaseEnums<Integer> {
    UNPAY(10, "未付款", true),
    WAIT_STOCK(20, "待出库", true),
    WAIT_CONFIRM(30, "待收货", true),
    WAIT_CHECK_CODE(40, "待核销", true),
    WAIT_COMMENT(50, "待评价", true),
    COMPLETE(60, "已完成", false),
    REFUNDING(70, "退款中", true),
    REFUNDED(80, "已退款", false),
    CANCELED(90, "已取消", false),

    ;

    OrderStatusType(int code, String msg, Boolean lockStock) {
        this.code = code;
        this.msg = msg;
        this.lockStock = lockStock;
    }

    private final int code;

    private final String msg;

    private final boolean lockStock;


    public Integer getCode() {
        return code;
    }

    /**
     * 判断定订单是否可退款
     * @return
     */
    public static boolean refundable(int orderStatus) {
        if (orderStatus == WAIT_STOCK.getCode() || orderStatus == WAIT_CONFIRM.getCode()) {
            return true;
        } else {
            return false;
        }
    }

    public static OrderStatusType getStatusByCode(int orderStatus) {
        for (OrderStatusType statusType : values()) {
            if (statusType.code == orderStatus) {
                return statusType;
            }
        }
        return null;
    }

    public static List<Integer> shipStatutes() {
        return Arrays.asList(WAIT_CHECK_CODE.getCode(), WAIT_COMMENT.getCode(), WAIT_CONFIRM.getCode(), COMPLETE.getCode());
    }

    public static List<Integer> settleStatutes() {
        return Arrays.asList(WAIT_COMMENT.getCode(), COMPLETE.getCode(), REFUNDED.getCode());
    }

    public static List<Integer> freezeStatutes() {
        // 12, 20, 30, 35, 60, 70
        return Arrays.asList(WAIT_STOCK.getCode(), WAIT_CONFIRM.getCode(), WAIT_CHECK_CODE.getCode(), REFUNDING.getCode());
    }

    public static List<Integer> settleAndFreezeStatutes() {
        List<Integer> list = new ArrayList<>();
        list.addAll(settleStatutes());
        list.addAll(freezeStatutes());
        return list;
    }

}
