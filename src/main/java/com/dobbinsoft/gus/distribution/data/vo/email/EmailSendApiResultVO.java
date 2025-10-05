package com.dobbinsoft.gus.distribution.data.vo.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendApiResultVO {

    @Schema(description = "消息ID")
    private String messageId;

} 