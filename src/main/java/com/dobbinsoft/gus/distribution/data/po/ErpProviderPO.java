package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpConfigModel;
import com.dobbinsoft.gus.distribution.data.enums.ErpProvider;
import com.dobbinsoft.gus.distribution.data.handler.AesTypeHandler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("ds_erp_provider")
@Schema(description = "ERP提供商实体")
public class ErpProviderPO extends BasePO {

    @Schema(description = "ERP提供商类型")
    private ErpProvider type;

    @Schema(description = "配置信息，需要AES加密")
    @TableField(typeHandler = AesTypeHandler.class)
    private String config;

    @Schema(description = "备注")
    private String remark;

    public JdyErpConfigModel asJdyErpConfigModel() {
        return JsonUtil.convertValue(config, JdyErpConfigModel.class);
    }
    
}
