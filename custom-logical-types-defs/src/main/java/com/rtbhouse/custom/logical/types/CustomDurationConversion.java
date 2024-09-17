package com.rtbhouse.custom.logical.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericFixed;

public class CustomDurationConversion extends Conversion<CustomDuration> {

    @Override
    public Class<CustomDuration> getConvertedType() {
        return CustomDuration.class;
    }

    @Override
    public String getLogicalTypeName() {
        return CustomDurationLogicalTypeFactory.LOGICAL_TYPE_NAME;
    }

    @Override
    public GenericFixed toFixed(CustomDuration customDuration, Schema schema, LogicalType type) {
        byte[] bytes = customDuration.serializeTo12Bytes();
        return new GenericData.Fixed(schema, bytes); // TwelveBytes class is in different Maven module
    }

    @Override
    public CustomDuration fromFixed(GenericFixed value, Schema schema, LogicalType type) {
        return CustomDuration.deserializeFrom12Bytes(value.bytes());
    }
}
