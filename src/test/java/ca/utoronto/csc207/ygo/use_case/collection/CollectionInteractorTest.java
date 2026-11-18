package ca.utoronto.csc207.ygo.use_case.collection;

import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.CollectionEntry;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CollectionInteractorTest {
    @Test
    void addingTheSameCardTwiceCombinesQuantities() {
        Map<Long, CollectionEntry> saved = new LinkedHashMap<>();

        CollectionStore fakeStore = new CollectionStore() {
            @Override
            public Map<Long, CollectionEntry> load() {
                return new LinkedHashMap<>(saved);
            }

            @Override
            public void save(Map<Long, CollectionEntry> entries) {
                saved.clear();
                saved.putAll(entries);
            }
        };

        CollectionOutputBoundary output = new CollectionOutputBoundary() {
            @Override
            public void present(CollectionResult result) {
            }

            @Override
            public void presentError(String message) {
                fail(message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(fakeStore, output);
        Card card = new Card(1L, "Test Card", "Monster", "", null, null, null);

        interactor.execute(CollectionRequest.add(card, 2));
        interactor.execute(CollectionRequest.add(card, 3));

        assertEquals(1, saved.size());
        assertEquals(5, saved.get(card.id()).quantity());
    }
    @Test
    void loadReturnsSavedCollection() {
        Card card = new Card(1L, "Test Card", "Monster", "", null, null, null);

        CollectionStore fakeStore = new CollectionStore() {
            @Override
            public Map<Long, CollectionEntry> load() {
                return Map.of(card.id(), new CollectionEntry(card, 5));
            }

            @Override
            public void save(Map<Long, CollectionEntry> entries) {
                fail("LOAD should not save anything.");
            }
        };

        Map<Long, CollectionEntry> displayed = new LinkedHashMap<>();
        CollectionOutputBoundary output = new CollectionOutputBoundary() {
            @Override
            public void present(CollectionResult result) {
                displayed.putAll(result.entries());
            }

            @Override
            public void presentError(String message) {
                fail(message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(fakeStore, output);
        interactor.execute(CollectionRequest.load());

        assertEquals(1, displayed.size());
        assertEquals(5, displayed.get(card.id()).quantity());
    }
    @Test
    void setReplacesSavedQuantity() {
        Card card = new Card(1L, "Test Card", "Monster", "", null, null, null);
        Map<Long, CollectionEntry> saved = new LinkedHashMap<>();
        saved.put(card.id(), new CollectionEntry(card, 5));

        CollectionStore fakeStore = new CollectionStore() {
            @Override
            public Map<Long, CollectionEntry> load() {
                return new LinkedHashMap<>(saved);
            }

            @Override
            public void save(Map<Long, CollectionEntry> entries) {
                saved.clear();
                saved.putAll(entries);
            }
        };

        Map<Long, CollectionEntry> displayed = new LinkedHashMap<>();
        CollectionOutputBoundary output = new CollectionOutputBoundary() {
            @Override
            public void present(CollectionResult result) {
                displayed.putAll(result.entries());
            }

            @Override
            public void presentError(String message) {
                fail(message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(fakeStore, output);
        interactor.execute(CollectionRequest.set(card.id(), 2));

        assertEquals(2, saved.get(card.id()).quantity());
        assertEquals(2, displayed.get(card.id()).quantity());
    }
    @Test
    void removeDeletesOnlyTheSelectedCard() {
        Card selected = new Card(1L, "Selected Card", "Monster", "", null, null, null);
        Card other = new Card(2L, "Other Card", "Monster", "", null, null, null);
        Map<Long, CollectionEntry> saved = new LinkedHashMap<>();
        saved.put(selected.id(), new CollectionEntry(selected, 2));
        saved.put(other.id(), new CollectionEntry(other, 3));

        CollectionStore fakeStore = new CollectionStore() {
            @Override
            public Map<Long, CollectionEntry> load() {
                return new LinkedHashMap<>(saved);
            }

            @Override
            public void save(Map<Long, CollectionEntry> entries) {
                saved.clear();
                saved.putAll(entries);
            }
        };

        Map<Long, CollectionEntry> displayed = new LinkedHashMap<>();
        CollectionOutputBoundary output = new CollectionOutputBoundary() {
            @Override
            public void present(CollectionResult result) {
                displayed.putAll(result.entries());
            }

            @Override
            public void presentError(String message) {
                fail(message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(fakeStore, output);
        interactor.execute(CollectionRequest.remove(selected.id()));

        assertEquals(1, saved.size());
        assertEquals(3, saved.get(other.id()).quantity());
        assertEquals(saved.keySet(), displayed.keySet());
    }
    @Test
    void failedSaveKeepsPreviousQuantity() {
        Card card = new Card(1L, "Test Card", "Monster", "", null, null, null);
        Map<Long, CollectionEntry> saved = new LinkedHashMap<>();
        saved.put(card.id(), new CollectionEntry(card, 5));

        CollectionStore fakeStore = new CollectionStore() {
            @Override
            public Map<Long, CollectionEntry> load() {
                return saved;
            }

            @Override
            public void save(Map<Long, CollectionEntry> entries) throws DataAccessException {
                throw new DataAccessException("Simulated save failure.");
            }
        };

        java.util.List<String> errors = new java.util.ArrayList<>();
        CollectionOutputBoundary output = new CollectionOutputBoundary() {
            @Override
            public void present(CollectionResult result) {
                fail("A failed save must not report success.");
            }

            @Override
            public void presentError(String message) {
                errors.add(message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(fakeStore, output);
        interactor.execute(CollectionRequest.set(card.id(), 2));

        assertEquals(5, saved.get(card.id()).quantity());
        assertEquals(1, errors.size());
    }
}