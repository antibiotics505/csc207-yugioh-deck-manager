package ca.utoronto.csc207.ygo.entity;

import java.util.Objects;

/** A card snapshot and its owned copy count. */
public final class CollectionEntry {
    private final Card card;
    private final int quantity;

    public CollectionEntry(Card card, int quantity) {
        this.card = Objects.requireNonNull(card, "card");
        this.quantity = Quantity.requireValid(quantity);
    }

    public Card card() { return card; }
    public int quantity() { return quantity; }

    public CollectionEntry withQuantity(int quantity) {
        return new CollectionEntry(card, quantity);
    }
}
