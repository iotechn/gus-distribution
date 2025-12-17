package com.dobbinsoft.gus.distribution.client.erp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 标准化的 ERP 事件
 *
 * <p>用于把各个 ERP 的回调（如：JDY）抽象成统一的事件结构，方便上层业务按类型处理。</p>
 */
@Getter
@Setter
public class ErpEvent {

    /**
     * 事件来源 ERP 类型（目前只有 JDY，预留扩展）
     */
    public enum Source {
        JDY
    }

    /**
     * 标准事件类型
     */
    public enum Type {
        /**
         * 库存变更（例如 JDY 的 inv_inventory_entity 回调）
         */
        INVENTORY_CHANGED,

        /**
         * 销货单创建 / 单据出库（例如 JDY 的 sal_bill_outbound 回调）
         */
        SALE_OUTBOUND_CREATED
    }

    /**
     * 事件来源 ERP
     */
    private Source source;

    /**
     * 标准事件类型
     */
    private Type type;

    /**
     * 原始 ERP 回调中的 bizType
     */
    private String bizType;

    /**
     * 原始 ERP 回调中的 operation
     */
    private String operation;

    /**
     * ERP 侧的租户/账套标识（如 JDY 的 accountId、tenantId 等）
     */
    private String erpTenantId;

    /**
     * 事件对应的 ERP 单据主键 ID（如 JDY 销货单 id）
     */
    private String erpDocumentId;

    /**
     * 发生事件的时间戳（来自 ERP 的原始回调，如 JDY 的 timestamp）
     */
    private Long occurredAt;

    // ==================== 分层的业务负载 ====================

    /**
     * 库存变更事件明细
     *
     * <p>当 {@link #type} 为 {@link Type#INVENTORY_CHANGED} 时有效。</p>
     */
    private InventoryChangedPayload inventoryChanged;

    /**
     * 销货单 / 出库单事件明细
     *
     * <p>当 {@link #type} 为 {@link Type#SALE_OUTBOUND_CREATED} 时有效。</p>
     */
    private SaleOutboundCreatedPayload saleOutboundCreated;

    /**
     * 库存变更负载
     */
    @Getter
    @Setter
    public static class InventoryChangedPayload {

        /**
         * 商品编码 / SKU（如 JDY 的 productNumber）
         */
        private String sku;

        /**
         * 仓库编码（如 JDY 的 locationNumber）
         */
        private String locationCode;

        /**
         * 当前库存数量（如 ERP 直接推送可用库存）
         */
        private java.math.BigDecimal quantity;
    }

    /**
     * 销货单 / 出库单创建负载
     */
    @Getter
    @Setter
    public static class SaleOutboundCreatedPayload {

        /**
         * ERP 单据主键 ID（如 JDY 销货单 id）
         */
        private String erpDocumentId;

        /**
         * 预留的来源单号等扩展字段（不同 ERP 可自行填充）
         */
        private String sourceOrder;
    }

}
