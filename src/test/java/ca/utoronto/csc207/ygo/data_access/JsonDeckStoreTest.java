package ca.utoronto.csc207.ygo.data_access;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.entity.DeckEntry;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDeckStoreTest {
    @TempDir
    Path tempDir;

    @Test
    void missingFileLoadsAsEmptyLibrary() throws Exception {
        JsonDeckStore store = new JsonDeckStore(tempDir.resolve("decks.json"));

        assertTrue(store.loadAll().isEmpty());
    }

    @Test
    void saveThenLoadPreservesDeckAndCardMetadata() throws Exception {
        Path file = tempDir.resolve("decks.json");
        JsonDeckStore store = new JsonDeckStore(file);
        UUID id = UUID.randomUUID();
        Card card = new Card(123L, "Dragon \"Ace\"", "Monster",
                "Line one\nLine two", 2500, 2000, 7);
        Map<Long, DeckEntry> entries = new LinkedHashMap<>();
        entries.put(card.id(), new DeckEntry(card, 3));
        Deck deck = new Deck(id, "My Deck", entries);
        Map<UUID, Deck> decks = new LinkedHashMap<>();
        decks.put(id, deck);

        store.saveAll(decks);
        Map<UUID, Deck> loaded = new JsonDeckStore(file).loadAll();

        Deck actual = loaded.get(id);
        assertEquals("My Deck", actual.name());
        assertEquals(3, actual.entries().get(card.id()).quantity());
        assertEquals("Dragon \"Ace\"", actual.entries().get(card.id()).card().name());
        assertEquals("Line one\nLine two",
                actual.entries().get(card.id()).card().description());
        assertEquals(2500, actual.entries().get(card.id()).card().attack().getAsInt());
    }

    @Test
    void malformedFileReportsDataAccessFailure() throws Exception {
        Path file = tempDir.resolve("decks.json");
        Files.writeString(file, "{not-json", StandardCharsets.UTF_8);

        assertThrows(DataAccessException.class, () -> new JsonDeckStore(file).loadAll());
    }
}
