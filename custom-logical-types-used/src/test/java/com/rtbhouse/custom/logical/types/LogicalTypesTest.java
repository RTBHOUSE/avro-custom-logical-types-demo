package com.rtbhouse.custom.logical.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import com.rtbhouse.avro.InMemoryEncoder;
import com.rtbhouse.generated.avro.RecordWithLogicalTypes;
import com.rtbhouse.generated.avro.StandardRecord;
import com.rtbhouse.generated.avro.TwelveBytes;

public class LogicalTypesTest {

    @Test
    void addingLogicalTypesDoesNotChangeSchemaCompatibility() {
        // given
        Schema schemaWithLogicalTypes = RecordWithLogicalTypes.getClassSchema();
        Schema schemaWithoutLogicalTypes = StandardRecord.getClassSchema();

        // expect
        assertThat(isCompatible(schemaWithLogicalTypes, schemaWithoutLogicalTypes)).isTrue();

        // and
        assertThat(isCompatible(schemaWithoutLogicalTypes, schemaWithLogicalTypes)).isTrue();
    }

    private boolean isCompatible(Schema reader, Schema writer) {
        return SchemaCompatibility.checkReaderWriterCompatibility(reader, writer)
                .getResult()
                .getCompatibility() == SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
    }

    @Test
    void usingLogicalTypesDoesNotChangeSerializedBytes() {
        // given
        UUID uuid = UUID.nameUUIDFromBytes("Miami".getBytes(StandardCharsets.UTF_8));
        String uuidAsString = uuid.toString();

        CustomDuration customDuration = new CustomDuration(3, 5, 34_000);
        TwelveBytes customDurationAsBytes = new TwelveBytes(customDuration.serializeTo12Bytes());

        Int2LongMultimap int2LongMultimap = new Int2LongMultimap() // Map<Integer, List<Long>>
                .add(111, 201L)
                .add(111, 202L)
                .add(33, 301L)
                .add(33, 302L)
                .add(33, 303L);

        // .toString() on keys from above map
        Map<String, List<Long>> avroMap = int2LongMultimap.toAvroMap();

        RecordWithLogicalTypes recordWithLogicalTypes = RecordWithLogicalTypes.newBuilder()
                .setUuid(uuid)
                .setCustomDuration(customDuration)
                .setInt2longMultimap(int2LongMultimap)
                .setArrayOfUnionOfCustomTypes(
                        Lists.newArrayList(null, uuid, customDuration, int2LongMultimap))
                .build();

        StandardRecord recordWithoutLogicalTypes = StandardRecord.newBuilder()
                .setUuid(uuidAsString)
                .setCustomDuration(customDurationAsBytes)
                .setInt2longMultimap(avroMap)
                .setArrayOfUnionOfCustomTypes(
                        Lists.newArrayList(null, uuidAsString, customDurationAsBytes, avroMap))
                .build();

        // when both records (with and without logical types) are serialized
        byte[] serializedRecordWithLogicalTypes = serializeSpecific(recordWithLogicalTypes);
        byte[] serializedRecordWithoutLogicalTypes = serializeSpecific(recordWithoutLogicalTypes);

        // then exactly the same byte-arrays are produced
        assertThat(serializedRecordWithLogicalTypes).isEqualTo(serializedRecordWithoutLogicalTypes);

        // when both records (with and without logical types) are deserialized from the same bytes-array
        RecordWithLogicalTypes deserializedRecordWithLogicalTypes = deserializeSpecific(
                serializedRecordWithLogicalTypes, RecordWithLogicalTypes.getClassSchema());

        StandardRecord deserializedStandardRecord = deserializeSpecific(
                serializedRecordWithLogicalTypes, StandardRecord.getClassSchema());

        // then deserialized records are equal to the origin records
        assertThat(deserializedRecordWithLogicalTypes).isEqualTo(recordWithLogicalTypes);
        assertThat(deserializedStandardRecord).isEqualTo(recordWithoutLogicalTypes);
    }

    private <T extends SpecificRecordBase> byte[] serializeSpecific(T record) {
        Schema schema = record.getSchema();
        SpecificDatumWriter<T> specificDatumWriter = new SpecificDatumWriter<>(schema);
        InMemoryEncoder inMemoryEncoder = new InMemoryEncoder();

        try {
            specificDatumWriter.write(record, inMemoryEncoder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return inMemoryEncoder.toByteArray();
    }

    private <T extends SpecificRecordBase> T deserializeSpecific(byte[] serializedRecord, Schema schema) {
        SpecificDatumReader<T> datumReader = new SpecificDatumReader<>(schema);

        try {
            return datumReader.read(null, DecoderFactory.get().binaryDecoder(serializedRecord, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
