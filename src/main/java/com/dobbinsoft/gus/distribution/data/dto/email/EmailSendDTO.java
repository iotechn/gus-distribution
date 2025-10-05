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
public class EmailSendDTO {

    @NotNull(message = "发件邮箱不能为空")
    @Schema(description = "发件邮箱ID")
    private String senderId;

    /**
     * true: 参数校验成功后直接在请求线程中发送
     * false：参数校验成功后，将发送任务投递到消息队列，再异步发送
     */
    @NotNull(message = "请选择是否同步发送")
    @Schema(description = "是否同步发送")
    private Boolean syncSend;

    @NotEmpty
    @Schema(description = "收件邮箱（不超过500个）")
    private List<String> toEmails;

    @Schema(description = "抄送邮箱（不超过20个）")
    private List<String> ccEmails;

    @NotBlank(message = "邮件主题不能为空")
    @Schema(description = "主题")
    private String subject;

    @Schema(description = "内容编码 空-不编码 base64-base64(UTF-8)")
    private String contentEncoding;

    @NotBlank(message = "邮件正文不能为空")
    @Schema(description = "正文")
    private String content;

    private Header header;

    @Getter
    @Setter
    public static class Header {

        @Schema(description = "引用InternetMessageId")
        private String references;

        @Schema(description = "回复InternetMessageId")
        private String inReplyTo;

    }
} 