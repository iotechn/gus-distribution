package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JdyAppAuthorize {

    @JsonProperty("bizType")
    private String bizType;
    @JsonProperty("data")
    private List<DataDTO> data;
    @JsonProperty("operation")
    private String operation;
    @JsonProperty("timestamp")
    private Long timestamp;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("accountId")
        private String accountId;
        @JsonProperty("accountName")
        private String accountName;
        @JsonProperty("agreementCompanyName")
        private String agreementCompanyName;
        @JsonProperty("appKey")
        private String appKey;
        @JsonProperty("appSecret")
        private String appSecret;
        @JsonProperty("clientId")
        private String clientId;
        @JsonProperty("domain")
        private String domain;
        @JsonProperty("externalNumber")
        private String externalNumber;
        @JsonProperty("instanceExpiresTime")
        private Long instanceExpiresTime;
        @JsonProperty("instanceId")
        private String instanceId;
        @JsonProperty("outerInstanceId")
        private String outerInstanceId;
        @JsonProperty("serviceId")
        private String serviceId;
        @JsonProperty("status")
        private Integer status;
        @JsonProperty("tid")
        private Integer tid;
    }
}
