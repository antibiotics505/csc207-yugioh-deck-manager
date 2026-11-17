package ca.utoronto.csc207.ygo.use_case.deck;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Deck;
import java.util.UUID;

/** Carries a draft snapshot for edits. SAVE also carries the current name field. */
public final class DeckRequest {
    public enum Action { LIST, OPEN, CREATE, ADD, SET, REMOVE, SAVE }

    private final Action action;
    private final Deck draft;
    private final UUID deckId;
    private final Card card;
    private final long cardId;
    private final int quantity;
    private final String name;

    private DeckRequest(Action action, Deck draft, UUID deckId, Card card,
                        long cardId, int quantity, String name) {
        this.action = action;
        this.draft = draft;
        this.deckId = deckId;
        this.card = card;
        this.cardId = cardId;
        this.quantity = quantity;
        this.name = name;
    }

    public static DeckRequest list() {
        return new DeckRequest(Action.LIST, null, null, null, 0, 0, null);
    }

    public static DeckRequest open(UUID deckId) {
        return new DeckRequest(Action.OPEN, null, deckId, null, 0, 0, null);
    }

    public static DeckRequest create() {
        return new DeckRequest(Action.CREATE, null, null, null, 0, 0, null);
    }

    public static DeckRequest add(Deck draft, Card card, int quantity) {
        return new DeckRequest(Action.ADD, draft, null, card, 0, quantity, null);
    }

    public static DeckRequest set(Deck draft, long cardId, int quantity) {
        return new DeckRequest(Action.SET, draft, null, null, cardId, quantity, null);
    }

    public static DeckRequest remove(Deck draft, long cardId) {
        return new DeckRequest(Action.REMOVE, draft, null, null, cardId, 0, null);
    }

    public static DeckRequest save(Deck draft, String name) {
        return new DeckRequest(Action.SAVE, draft, null, null, 0, 0, name);
    }

    public Action action() { return action; }
    public Deck draft() { return draft; }
    public UUID deckId() { return deckId; }
    public Card card() { return card; }
    public long cardId() { return cardId; }
    public int quantity() { return quantity; }
    public String name() { return name; }
}
