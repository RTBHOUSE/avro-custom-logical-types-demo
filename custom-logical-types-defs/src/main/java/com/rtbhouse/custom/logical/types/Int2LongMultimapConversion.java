package com.rtbhouse.custom.logical.types;

import java.util.Map;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class Int2LongMultimapConversion extends Conversion<Int2LongMultimap> {

    @Override
    public Class<Int2LongMultimap> getConvertedType() {
        return Int2LongMultimap.class;
    }

    @Override
    public String getLogicalTypeName() {
        return Int2LongMultimapLogicalTypeFactory.LOGICAL_TYPE_NAME;
    }

    @Override
    public Map<?, ?> toMap(Int2LongMultimap int2LongMultimap, Schema schema, LogicalType type) {
        return int2LongMultimap.toAvroMap();
    }

    @Override
    public Int2LongMultimap fromMap(Map<?, ?> vanillaAvroMap, Schema schema, LogicalType type) {
        return Int2LongMultimap.fromAvroMap(vanillaAvroMap);
    }
}
