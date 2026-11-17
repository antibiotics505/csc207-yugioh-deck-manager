package ca.utoronto.csc207.ygo.use_case.collection;

import ca.utoronto.csc207.ygo.entity.CollectionEntry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CollectionResult {
    private final Map<Long, CollectionEntry> entries;

    public CollectionResult(Map<Long, CollectionEntry> entries) {
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    public Map<Long, CollectionEntry> entries() { return entries; }
}
