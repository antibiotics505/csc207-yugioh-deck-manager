package ca.utoronto.csc207.ygo.entity;

import java.util.Objects;

/** A card snapshot and its planned copy count. */
public final class DeckEntry {
    private final Card card;
    private final int quantity;

    public DeckEntry(Card card, int quantity) {
        this.card = Objects.requireNonNull(card, "card");
        this.quantity = Quantity.requireValid(quantity);
    }

    public Card card() { return card; }
    public int quantity() { return quantity; }

    public DeckEntry withQuantity(int quantity) {
        return new DeckEntry(card, quantity);
    }
}
