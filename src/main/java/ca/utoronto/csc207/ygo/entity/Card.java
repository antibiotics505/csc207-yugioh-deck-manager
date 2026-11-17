package ca.utoronto.csc207.ygo.entity;

import java.util.Objects;
import java.util.OptionalInt;

/** An immutable snapshot of a catalog card. Missing combat stats are valid. */
public final class Card {
    private final long id;
    private final String name;
    private final String type;
    private final String description;
    private final Integer attack;
    private final Integer defense;
    private final Integer level;

    public Card(long id, String name, String type, String description,
                Integer attack, Integer defense, Integer level) {
        if (id <= 0) {
            throw new IllegalArgumentException("Card ID must be positive.");
        }
        this.id = id;
        this.name = requiredText(name, "Card name");
        this.type = requiredText(type, "Card type");
        this.description = Objects.requireNonNull(description, "description");
        this.attack = attack;
        this.defense = defense;
        this.level = level;
    }

    public long id() { return id; }
    public String name() { return name; }
    public String type() { return type; }
    public String description() { return description; }
    public OptionalInt attack() { return optional(attack); }
    public OptionalInt defense() { return optional(defense); }
    public OptionalInt level() { return optional(level); }

    private static String requiredText(String value, String field) {
        String text = Objects.requireNonNull(value, field).strip();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank.");
        }
        return text;
    }

    private static OptionalInt optional(Integer value) {
        return value == null ? OptionalInt.empty() : OptionalInt.of(value);
    }
}
