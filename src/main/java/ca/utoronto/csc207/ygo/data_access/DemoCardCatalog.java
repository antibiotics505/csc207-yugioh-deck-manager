package ca.utoronto.csc207.ygo.data_access;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.use_case.search.CardCatalog;
import ca.utoronto.csc207.ygo.use_case.search.SearchResult;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Small offline fixture for the Catalog demo. */
public final class DemoCardCatalog implements CardCatalog {
    private static final List<Card> CARDS = List.of(
            new Card(46986414, "Dark Magician", "Normal Monster",
                    "Sample Spellcaster card.", 2500, 2100, 7),
            new Card(38033121, "Dark Magician Girl", "Effect Monster",
                    "Sample Spellcaster card.", 2000, 1700, 6),
            new Card(89631139, "Blue-Eyes White Dragon", "Normal Monster",
                    "Sample Dragon card.", 3000, 2500, 8));

    @Override
    public SearchResult search(String query, int limit) {
        String needle = query.toLowerCase(Locale.ROOT);
        List<Card> matches = CARDS.stream()
                .filter(card -> card.name().toLowerCase(Locale.ROOT).contains(needle))
                .limit(limit)
                .collect(Collectors.toList());
        return new SearchResult(matches, false);
    }
}
