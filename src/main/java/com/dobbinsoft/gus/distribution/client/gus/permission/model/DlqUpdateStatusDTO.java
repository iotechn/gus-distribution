package com.dobbinsoft.gus.distribution.client.gus.permission.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 死信队列状态更新DTO
 */
@Data
@Schema(description = "Dead Letter Queue Status Update DTO")
public class DlqUpdateStatusDTO {

    @NotBlank(message = "DLQ record ID cannot be blank")
    @Schema(description = "Dead Letter Queue record ID", example = "123456789", required = true)
    private String dlqRecordId;

    @NotNull(message = "Status cannot be null")
    @Schema(description = "New status", example = "PROCESSING", required = true)
    private Status status;

    /**
     * 状态枚举
     */
    public enum Status {
        /**
         * 待处理
         */
        PENDING,

        /**
         * 处理中
         */
        PROCESSING,

        /**
         * 成功
         */
        SUCCESS,

        /**
         * 失败
         */
        FAILED
    }

}
