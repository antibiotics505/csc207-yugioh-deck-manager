package ca.utoronto.csc207.ygo.use_case.deck;

import ca.utoronto.csc207.ygo.entity.Deck;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** LIST/SAVE may refresh the saved list. OPEN/CREATE/edits return the active draft. */
public final class DeckResult {
    private final DeckRequest.Action action;
    private final Deck draft;
    private final Map<UUID, Deck> savedDecks;

    public DeckResult(DeckRequest.Action action, Deck draft, Map<UUID, Deck> savedDecks) {
        this.action = Objects.requireNonNull(action, "action");
        this.draft = draft;
        this.savedDecks = savedDecks == null ? null
                : Collections.unmodifiableMap(new LinkedHashMap<>(savedDecks));
    }

    public DeckRequest.Action action() { return action; }
    public Optional<Deck> draft() { return Optional.ofNullable(draft); }

    /** Absent means keep the prior list. An empty map clears it. */
    public Optional<Map<UUID, Deck>> savedDecks() { return Optional.ofNullable(savedDecks); }
}
