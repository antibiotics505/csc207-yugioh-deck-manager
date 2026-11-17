package ca.utoronto.csc207.ygo.entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** A deck snapshot. Each map key must equal the corresponding card ID. */
public final class Deck {
    private final UUID id;
    private final String name;
    private final Map<Long, DeckEntry> entries;

    public Deck(UUID id, String name, Map<Long, DeckEntry> entries) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name").strip();
        if (this.name.isEmpty()) {
            throw new IllegalArgumentException("Deck name must not be blank.");
        }
        Map<Long, DeckEntry> copy = new LinkedHashMap<>(entries);
        for (Map.Entry<Long, DeckEntry> entry : copy.entrySet()) {
            long cardId = Objects.requireNonNull(entry.getKey(), "card ID");
            if (cardId != entry.getValue().card().id()) {
                throw new IllegalArgumentException("Entry key must match the card ID.");
            }
        }
        this.entries = Collections.unmodifiableMap(copy);
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public Map<Long, DeckEntry> entries() { return entries; }

    public Deck withEntry(DeckEntry entry) {
        Map<Long, DeckEntry> updated = new LinkedHashMap<>(entries);
        updated.put(entry.card().id(), entry);
        return new Deck(id, name, updated);
    }

    public Deck withoutEntry(long cardId) {
        Map<Long, DeckEntry> updated = new LinkedHashMap<>(entries);
        updated.remove(cardId);
        return new Deck(id, name, updated);
    }

    public Deck withName(String name) {
        return new Deck(id, name, entries);
    }

    public int totalCopies() {
        return entries.values().stream().mapToInt(DeckEntry::quantity).sum();
    }
}
