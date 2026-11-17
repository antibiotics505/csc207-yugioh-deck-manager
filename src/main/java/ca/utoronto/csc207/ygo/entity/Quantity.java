package ca.utoronto.csc207.ygo.entity;

/** Application storage bounds shared by owned and planned quantities. */
public final class Quantity {
    public static final int MIN = 1;
    public static final int MAX = 999;

    private Quantity() {}

    public static int requireValid(int quantity) {
        if (quantity < MIN || quantity > MAX) {
            throw new IllegalArgumentException("Quantity must be between " + MIN + " and " + MAX + ".");
        }
        return quantity;
    }
}
