package com.dobbinsoft.gus.distribution.client.gus.file;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dobbinsoft.gus.distribution.client.gus.file.model.FileItemVO;
import com.dobbinsoft.gus.distribution.config.OpenFeignConfig;
import com.dobbinsoft.gus.web.vo.R;

import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(
        name = "gus-file", 
        url = "${gus.distribution.file-url}", 
        path = "/api/file",
        configuration = OpenFeignConfig.class)
public interface FileFeignClient {
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<FileItemVO> uploadFile(
            @Parameter(description = "上传的文件") @RequestPart("file") MultipartFile file,
            @Parameter(description = "是否私有，必传") @RequestPart("privateFile") Boolean privateFile,
            @Parameter(description = "文件所在文件夹") @RequestPart("folder") String folder,
            @Parameter(description = "过期时间（秒），可选。如果提供且 > 0，则创建临时文件；否则创建永久文件") @RequestPart(value = "expirySeconds", required = false) Integer expirySeconds,
            @Parameter(description = "自定义文件名，可选。如果提供，优先级高于file的OriginalFilename") @RequestPart(value = "customFileName", required = false) String customFileName);

    @PostMapping("/{fileId}/confirm")
    R<FileItemVO> confirmFile(
            @Parameter(description = "文件ID") @PathVariable("fileId") String fileId);
            
}
