package ca.utoronto.csc207.ygo.data_access;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.entity.DeckEntry;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import ca.utoronto.csc207.ygo.use_case.deck.DeckStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Stores deck snapshots as UTF-8 JSON. */
public final class JsonDeckStore implements DeckStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;

    public JsonDeckStore() {
        this(Path.of(System.getProperty("user.home"), ".csc207-ygo", "decks.json"));
    }

    public JsonDeckStore(Path file) {
        this.file = file.toAbsolutePath();
    }

    @Override
    public Map<UUID, Deck> loadAll() throws DataAccessException {
        if (!Files.exists(file)) return new LinkedHashMap<>();

        try {
            JsonFile saved = GSON.fromJson(
                    Files.readString(file, StandardCharsets.UTF_8), JsonFile.class);
            if (saved == null || saved.schemaVersion != 1 || saved.decks == null) {
                throw new IllegalArgumentException("Unsupported or malformed deck file.");
            }

            Map<UUID, Deck> result = new LinkedHashMap<>();
            for (DeckData data : saved.decks) {
                if (data == null || data.id == null || data.name == null || data.entries == null) {
                    throw new IllegalArgumentException("Malformed deck entry.");
                }
                UUID id = UUID.fromString(data.id);
                Map<Long, DeckEntry> entries = new LinkedHashMap<>();
                for (EntryData entryData : data.entries) {
                    if (entryData == null || entryData.card == null || entryData.quantity == null) {
                        throw new IllegalArgumentException("Malformed deck card entry.");
                    }
                    CardData cardData = entryData.card;
                    Card card = new Card(cardData.id, cardData.name, cardData.type,
                            cardData.description, cardData.atk, cardData.def, cardData.level);
                    DeckEntry entry = new DeckEntry(card, entryData.quantity);
                    if (entries.put(card.id(), entry) != null) {
                        throw new IllegalArgumentException("Duplicate card in deck.");
                    }
                }
                Deck deck = new Deck(id, data.name, entries);
                if (result.put(id, deck) != null) {
                    throw new IllegalArgumentException("Duplicate deck ID.");
                }
            }
            return result;
        } catch (IOException | RuntimeException e) {
            throw new DataAccessException("Could not read saved decks.", e);
        }
    }

    @Override
    public void saveAll(Map<UUID, Deck> decks) throws DataAccessException {
        JsonFile saved = new JsonFile();
        saved.schemaVersion = 1;
        saved.decks = new ArrayList<>();
        for (Deck deck : decks.values()) {
            DeckData deckData = new DeckData();
            deckData.id = deck.id().toString();
            deckData.name = deck.name();
            deckData.entries = new ArrayList<>();
            for (DeckEntry entry : deck.entries().values()) {
                EntryData entryData = new EntryData();
                entryData.quantity = entry.quantity();
                Card card = entry.card();
                CardData cardData = new CardData();
                cardData.id = card.id();
                cardData.name = card.name();
                cardData.type = card.type();
                cardData.description = card.description();
                cardData.atk = card.attack().isPresent() ? card.attack().getAsInt() : null;
                cardData.def = card.defense().isPresent() ? card.defense().getAsInt() : null;
                cardData.level = card.level().isPresent() ? card.level().getAsInt() : null;
                entryData.card = cardData;
                deckData.entries.add(entryData);
            }
            saved.decks.add(deckData);
        }

        Path temporary = null;
        try {
            Files.createDirectories(file.getParent());
            temporary = Files.createTempFile(file.getParent(), "decks-", ".tmp");
            Files.writeString(temporary, GSON.toJson(saved), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not save decks.", e);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); }
                catch (IOException ignored) { /* Best-effort temporary-file cleanup. */ }
            }
        }
    }

    private static final class JsonFile {
        private int schemaVersion;
        private List<DeckData> decks;
    }

    private static final class DeckData {
        private String id;
        private String name;
        private List<EntryData> entries;
    }

    private static final class EntryData {
        private Integer quantity;
        private CardData card;
    }

    private static final class CardData {
        private long id;
        private String name;
        private String type;
        private String description;
        private Integer atk;
        private Integer def;
        private Integer level;
    }
}
