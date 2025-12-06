package com.dobbinsoft.gus.distribution.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.distribution.data.dto.session.BoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    private String currentUserId() {
        BoSessionInfoDTO bo = SessionUtils.getBoSession();
        if (bo != null && bo.getUserId() != null) return bo.getUserId();
        FoSessionInfoDTO fo = SessionUtils.getFoSession();
        if (fo != null && fo.getUserId() != null) return fo.getUserId();
        return "system";
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        this.strictInsertFill(metaObject, "createdTime", ZonedDateTime.class, now);
        this.strictInsertFill(metaObject, "modifiedTime", ZonedDateTime.class, now);
        this.strictInsertFill(metaObject, "createdBy", String.class, currentUserId());
        this.strictInsertFill(metaObject, "modifiedBy", String.class, currentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        this.strictUpdateFill(metaObject, "modifiedTime", ZonedDateTime.class, now);
        this.strictUpdateFill(metaObject, "modifiedBy", String.class, currentUserId());
    }
}

