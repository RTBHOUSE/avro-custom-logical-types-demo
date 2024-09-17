package com.rtbhouse.custom.logical.types;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

public class Int2LongMultimapLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {

    static final String LOGICAL_TYPE_NAME = "int-2-long-multimap";

    public static final LogicalType TYPE = new LogicalType(LOGICAL_TYPE_NAME) {
        @Override
        public void validate(Schema schema) {
            if (schema.getType() != Schema.Type.MAP) {
                throw new IllegalArgumentException(LOGICAL_TYPE_NAME + " can only be used with an underlying MAP type");
            }
        }
    };

    @Override
    public LogicalType fromSchema(Schema schema) {
        return TYPE;
    }

    @Override
    public String getTypeName() {
        return LOGICAL_TYPE_NAME;
    }
}

