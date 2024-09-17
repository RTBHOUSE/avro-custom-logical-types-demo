package com.rtbhouse.custom.logical.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record CustomDuration(int months, int days, int millis) implements Comparable<CustomDuration> {

    @Override
    public int compareTo(CustomDuration other) {
        int res = Integer.compare(this.months, other.months);
        if (res == 0) {
            res = Integer.compare(this.days, other.days);
            if (res == 0) {
                res = Integer.compare(this.millis, other.millis);
            }
        }

        return res;
    }

    public byte[] serializeTo12Bytes() {
        return ByteBuffer.wrap(new byte[12])
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(months)
                .putInt(days)
                .putInt(millis)
                .array();
    }

    public static CustomDuration deserializeFrom12Bytes(byte[] bytes) {
        ByteBuffer littleEndianBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return new CustomDuration(
                littleEndianBuffer.getInt(),
                littleEndianBuffer.getInt(),
                littleEndianBuffer.getInt());
    }
}
