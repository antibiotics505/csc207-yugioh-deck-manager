package ca.utoronto.csc207.ygo.use_case.search;

import ca.utoronto.csc207.ygo.entity.Card;
import java.util.List;

public final class SearchResult {
    private final List<Card> cards;
    private final boolean cached;

    public SearchResult(List<Card> cards, boolean cached) {
        this.cards = List.copyOf(cards);
        this.cached = cached;
    }

    public List<Card> cards() { return cards; }
    public boolean cached() { return cached; }
}
