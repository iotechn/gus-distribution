package com.dobbinsoft.gus.distribution.client.gus.file;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dobbinsoft.gus.distribution.client.gus.file.model.FileItemVO;
import com.dobbinsoft.gus.web.vo.R;

import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(name = "gus-file", url = "${gus.distribution.file-url}", path = "/api/file")
public interface FileFeignClient {
    
    @PostMapping("/upload")
    R<FileItemVO> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "是否私有，必传") @RequestParam("privateFile") Boolean privateFile,
            @Parameter(description = "文件所在文件夹") @RequestParam("folder") String folder,
            @Parameter(description = "过期时间（秒），可选。如果提供且 > 0，则创建临时文件；否则创建永久文件") @RequestParam(value = "expirySeconds", required = false) Integer expirySeconds);

    @PostMapping("/{fileId}/confirm")
    R<FileItemVO> confirmFile(
            @Parameter(description = "文件ID") @PathVariable("fileId") String fileId);
            
}
