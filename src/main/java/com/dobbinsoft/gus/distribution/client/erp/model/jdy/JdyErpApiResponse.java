package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import lombok.Data;

/**
 * ERP API通用响应DTO
 */
@Data
public class JdyErpApiResponse<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 总记录数
     */
    private Long totalCount;

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }
}

 