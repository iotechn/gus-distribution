package com.dobbinsoft.gus.distribution.client.erp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErpItemAttr {

    private String demoJson;

    private String documentUrl;

    private List<Attr> attrs;


    @Getter
    @Setter
    public static class Attr {

        private String name;

        private Object demoValue;

        private DataType dataType;


    }

    public enum DataType {
        STRING,
        BIG_DECIMAL,
        INTEGER,
        BOOLEAN,
        ARRAY_UNKNOWN,
        ARRAY_STRING,
        ARRAY_BIG_DECIMAL,
        ARRAY_INTEGER,
        ARRAY_BOOLEAN,
    }

}
