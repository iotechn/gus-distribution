package com.dobbinsoft.gus.distribution.client.gus.permission.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 死信队列创建DTO
 */
@Data
@Schema(description = "Dead Letter Queue Create DTO")
public class DlqCreateDTO {

    @NotBlank(message = "Message type cannot be blank")
    @Schema(description = "Message type", example = "ITEM_SYNC", required = true)
    private String messageType;

    @NotBlank(message = "Stream key cannot be blank")
    @Schema(description = "Stream key", example = "item:sync:stream", required = true)
    private String streamKey;

    @NotBlank(message = "Message ID cannot be blank")
    @Schema(description = "Message ID", example = "msg_123456789", required = true)
    private String messageId;

    @Schema(description = "Message value (JSON format)", example = "{\"itemId\": \"123\", \"action\": \"update\"}")
    private String messageValue;

    @Schema(description = "Error message", example = "Connection timeout")
    private String errorMessage;
}
