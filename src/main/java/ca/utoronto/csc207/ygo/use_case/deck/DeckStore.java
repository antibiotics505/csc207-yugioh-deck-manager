package ca.utoronto.csc207.ygo.use_case.deck;

import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import java.util.Map;
import java.util.UUID;

public interface DeckStore {
    /** Returns a detached snapshot by deck UUID. A missing file is an empty library. */
    Map<UUID, Deck> loadAll() throws DataAccessException;

    /** Replaces the saved library. On failure the last committed data remains intact. */
    void saveAll(Map<UUID, Deck> decks) throws DataAccessException;
}
