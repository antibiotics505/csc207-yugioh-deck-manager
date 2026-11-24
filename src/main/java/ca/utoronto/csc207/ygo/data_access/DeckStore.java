package ca.utoronto.csc207.ygo.data_access;

import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class DeckStore
        implements ca.utoronto.csc207.ygo.use_case.deck.DeckStore {

    private Map<UUID, Deck> decks = new LinkedHashMap<>();

    @Override
    public Map<UUID, Deck> loadAll() throws DataAccessException {
        return new LinkedHashMap<>(decks);
    }

    @Override
    public void saveAll(Map<UUID, Deck> decks) throws DataAccessException {
        this.decks = new LinkedHashMap<>(decks);
    }
}

