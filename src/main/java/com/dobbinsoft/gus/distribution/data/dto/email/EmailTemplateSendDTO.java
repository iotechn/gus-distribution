package com.dobbinsoft.gus.distribution.data.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailTemplateSendDTO {

    @NotNull(message = "发件邮箱不能为空")
    @Schema(description = "发件邮箱ID")
    private String senderId;

    @NotEmpty
    @Schema(description = "收件邮箱（不超过500个）")
    private List<String> toEmails;

    @Schema(description = "抄送邮箱（不超过20个）")
    private List<String> ccEmails;

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "引用模板ID")
    private String templateId;

    @NotBlank(message = "邮件主题不能为空")
    @Schema(description = "主题")
    private String subject;

    @Schema(description = "参数")
    private List<EmailTemplateParamDTO> params;

} 