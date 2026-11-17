package ca.utoronto.csc207.ygo.use_case.collection;

import ca.utoronto.csc207.ygo.entity.CollectionEntry;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import java.util.Map;

public interface CollectionStore {
    /** Returns a detached snapshot by card ID. A missing file is an empty collection. */
    Map<Long, CollectionEntry> load() throws DataAccessException;

    /** Replaces the saved collection. On failure the last committed data remains intact. */
    void save(Map<Long, CollectionEntry> entries) throws DataAccessException;
}
