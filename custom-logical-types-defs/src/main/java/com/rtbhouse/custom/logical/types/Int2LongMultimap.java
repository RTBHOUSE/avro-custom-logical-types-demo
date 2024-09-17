package com.rtbhouse.custom.logical.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Int2LongMultimap extends LinkedHashMap<Integer, List<Long>> {

    public Int2LongMultimap add(Integer key, Long value) {
        super.computeIfAbsent(key, __ -> new ArrayList<>()).add(value);
        return this;
    }

    public Map<String, List<Long>> toAvroMap() {
        Map<String, List<Long>> avroMap = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<Long>> entry : entrySet()) {
            String stringKey = entry.getKey().toString();
            avroMap.put(stringKey, entry.getValue());
        }

        return avroMap;
    }

    public static Int2LongMultimap fromAvroMap(Map<?, ?> avroMap) {
        Int2LongMultimap int2LongMultimap = new Int2LongMultimap();

        for (Map.Entry<?, ?> entry : avroMap.entrySet()) {
            int intKey = Integer.parseInt(entry.getKey().toString());
            @SuppressWarnings("unchecked")
            List<Long> longs = (List<Long>) entry.getValue();

            int2LongMultimap.put(intKey, longs);
        }

        return int2LongMultimap;
    }
}
