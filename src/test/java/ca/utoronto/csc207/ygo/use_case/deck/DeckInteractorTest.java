package ca.utoronto.csc207.ygo.use_case.deck;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.entity.DeckEntry;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckInteractorTest {
    private static final Card CARD = new Card(
            123L, "Test Dragon", "Monster", "A test card", 1000, 800, 4);

    @Test
    void createReturnsAnEmptyDraftWithFreshId() {
        Fixture fixture = new Fixture();

        fixture.interactor.execute(DeckRequest.create());

        Deck draft = fixture.output.result.draft().orElseThrow();
        assertNotNull(draft.id());
        assertEquals("Untitled Deck", draft.name());
        assertTrue(draft.entries().isEmpty());
        assertFalse(fixture.output.error != null);
    }

    @Test
    void addIncrementsQuantityForAnExistingCard() {
        Fixture fixture = new Fixture();
        Deck draft = deckWith(CARD, 2);

        fixture.interactor.execute(DeckRequest.add(draft, CARD, 3));

        Deck updated = fixture.output.result.draft().orElseThrow();
        assertEquals(5, updated.entries().get(CARD.id()).quantity());
        assertEquals(5, updated.totalCopies());
    }

    @Test
    void saveStoresNamedDeckAndOpenReturnsIt() {
        Fixture fixture = new Fixture();
        Deck draft = deckWith(CARD, 2);

        fixture.interactor.execute(DeckRequest.save(draft, "  My Deck  "));
        fixture.interactor.execute(DeckRequest.open(draft.id()));

        Deck reopened = fixture.output.result.draft().orElseThrow();
        assertEquals("My Deck", reopened.name());
        assertEquals(2, reopened.entries().get(CARD.id()).quantity());
    }

    @Test
    void blankSaveNameReportsErrorAndDoesNotSave() {
        Fixture fixture = new Fixture();
        Deck draft = deckWith(CARD, 1);

        fixture.interactor.execute(DeckRequest.save(draft, "  "));

        assertNotNull(fixture.output.error);
        assertTrue(fixture.store.decks.isEmpty());
    }

    @Test
    void setReplacesExistingCardQuantity() {
        Fixture fixture = new Fixture();
        Deck draft = deckWith(CARD, 2);

        fixture.interactor.execute(DeckRequest.set(draft, CARD.id(), 4));

        Deck updated = fixture.output.result.draft().orElseThrow();
        assertEquals(4, updated.entries().get(CARD.id()).quantity());
    }

    @Test
    void removeDeletesExistingCard() {
        Fixture fixture = new Fixture();
        Deck draft = deckWith(CARD, 2);

        fixture.interactor.execute(DeckRequest.remove(draft, CARD.id()));

        Deck updated = fixture.output.result.draft().orElseThrow();
        assertTrue(updated.entries().isEmpty());
        assertEquals(0, updated.totalCopies());
    }

    @Test
    void addRejectsQuantityAboveLimit() {
        Fixture fixture = new Fixture();

        fixture.interactor.execute(DeckRequest.add(deckWith(CARD, 1), CARD, 1000));

        assertNotNull(fixture.output.error);
        assertTrue(fixture.store.decks.isEmpty());
    }

    @Test
    void openMissingDeckReportsError() {
        Fixture fixture = new Fixture();

        fixture.interactor.execute(DeckRequest.open(UUID.randomUUID()));

        assertNotNull(fixture.output.error);
        assertTrue(fixture.output.result == null);
    }

    @Test
    void saveFailureReportsErrorAndKeepsStoreUnchanged() {
        Fixture fixture = new Fixture();
        fixture.store.failOnSave = true;

        fixture.interactor.execute(DeckRequest.save(deckWith(CARD, 1), "My Deck"));

        assertNotNull(fixture.output.error);
        assertTrue(fixture.store.decks.isEmpty());
    }

    private static Deck deckWith(Card card, int quantity) {
        Map<Long, DeckEntry> entries = new LinkedHashMap<>();
        entries.put(card.id(), new DeckEntry(card, quantity));
        return new Deck(UUID.randomUUID(), "Draft", entries);
    }

    private static final class Fixture {
        private final FakeStore store = new FakeStore();
        private final RecordingOutput output = new RecordingOutput();
        private final DeckInteractor interactor = new DeckInteractor(store, output);
    }

    private static final class FakeStore implements DeckStore {
        private Map<UUID, Deck> decks = new LinkedHashMap<>();
        private boolean failOnSave;

        @Override
        public Map<UUID, Deck> loadAll() {
            return new LinkedHashMap<>(decks);
        }

        @Override
        public void saveAll(Map<UUID, Deck> decks) throws DataAccessException {
            if (failOnSave) {
                throw new DataAccessException("Simulated storage failure.");
            }
            this.decks = new LinkedHashMap<>(decks);
        }
    }

    private static final class RecordingOutput implements DeckOutputBoundary {
        private DeckResult result;
        private String error;

        @Override
        public void present(DeckResult result) {
            this.result = result;
            this.error = null;
        }

        @Override
        public void presentError(String message) {
            this.error = message;
            this.result = null;
        }
    }
}
