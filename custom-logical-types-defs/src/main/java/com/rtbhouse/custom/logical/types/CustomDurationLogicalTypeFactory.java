package com.rtbhouse.custom.logical.types;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

public class CustomDurationLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {

    static final String LOGICAL_TYPE_NAME = "custom-duration";

    public static final LogicalType TYPE = new LogicalType(LOGICAL_TYPE_NAME) {
        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.FIXED) {
                throw new IllegalArgumentException(LOGICAL_TYPE_NAME + " can only be used with an underlying FIXED type");
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

