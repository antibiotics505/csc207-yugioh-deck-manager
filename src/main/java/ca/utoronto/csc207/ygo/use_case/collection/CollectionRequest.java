package ca.utoronto.csc207.ygo.use_case.collection;

import ca.utoronto.csc207.ygo.entity.Card;

/** ADD carries a selected Card. SET and REMOVE identify an existing row by ID. */
public final class CollectionRequest {
    public enum Action { LOAD, ADD, SET, REMOVE }

    private final Action action;
    private final Card card;
    private final long cardId;
    private final int quantity;

    private CollectionRequest(Action action, Card card, long cardId, int quantity) {
        this.action = action;
        this.card = card;
        this.cardId = cardId;
        this.quantity = quantity;
    }

    public static CollectionRequest load() {
        return new CollectionRequest(Action.LOAD, null, 0, 0);
    }

    public static CollectionRequest add(Card card, int quantity) {
        return new CollectionRequest(Action.ADD, card, 0, quantity);
    }

    public static CollectionRequest set(long cardId, int quantity) {
        return new CollectionRequest(Action.SET, null, cardId, quantity);
    }

    public static CollectionRequest remove(long cardId) {
        return new CollectionRequest(Action.REMOVE, null, cardId, 0);
    }

    public Action action() { return action; }
    public Card card() { return card; }
    public long cardId() { return cardId; }
    public int quantity() { return quantity; }
}
