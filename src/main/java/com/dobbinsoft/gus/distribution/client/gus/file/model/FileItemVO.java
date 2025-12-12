package com.dobbinsoft.gus.distribution.client.gus.file.model;

import java.time.ZonedDateTime;

import lombok.Data;

/**
 * 文件项VO
 */
@Data
public class FileItemVO {

    /**
     * 文件/文件夹ID
     */
    private String id;

    /**
     * 文件/文件夹名称
     */
    private String name;

    /**
     * 类型：FILE-文件，FOLDER-文件夹
     */
    private FileType type;

    /**
     * 父级ID，根目录为NULL
     */
    private String parentId;

    /**
     * 完整路径，如：/folder1/folder2/file.txt
     */
    private String path;

    /**
     * 文件大小（字节），文件夹为NULL
     */
    private Long fileSize;

    /**
     * 文件大小格式化显示
     */
    private String fileSizeFormatted;

    /**
     * 文件扩展名，文件夹为NULL
     */
    private String fileExtension;

    /**
     * MIME类型，文件夹为NULL
     */
    private String mimeType;

    /**
     * MinIO存储路径，文件夹为NULL
     */
    private String storagePath;

    /**
     * 文件MD5哈希值，文件夹为NULL
     */
    private String md5Hash;

    /**
     * HTTP协议的的contentType
     */
    private String contentType;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    private ZonedDateTime createTime;

    /**
     * 更新时间
     */
    private ZonedDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 是否为文件夹
     */
    private Boolean isFolder;

    /**
     * 是否为文件
     */
    private Boolean isFile;

    /**
     * 文件状态：TEMP 临时，PERMANENT 永久
     * 临时文件用于解决用户上传文件但未提交表单的场景，可以通过确认接口转为永久文件
     * 临时文件会在过期后自动清理
     */
    private Status status;

    /**
     * 过期时间（仅对TEMP状态的文件有效）
     * 默认3天过期，最长30天
     */
    private ZonedDateTime expireAt;

    public enum FileType {
        FILE,
        FOLDER
    }

    /**
     * 文件状态枚举
     */
    public enum Status {
        /**
         * 临时文件：用户上传但未确认的文件，会在过期后自动清理
         */
        TEMP,
        /**
         * 永久文件：已确认的文件，不会被自动清理
         */
        PERMANENT
    }
}
