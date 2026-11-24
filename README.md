# Yu-Gi-Oh! Collection and Deck Manager

CSC207 Fall 2026 team project. This Java Swing desktop application helps players find cards, track the copies they own, and build named deck lists. Owned and planned quantities are independent. For example, a player can own two copies of Dark Magician and plan to use one in a deck.

## Current status

The Catalog tab searches YGOPRODeck by card name and shows details when you select a result. Try `dark magician` or `NoSuchCatalogCard928315`. Card images, cache persistence, Collection and Deck features remain pending.

## Build and launch

Requirements are JDK 11 or later, Maven and a desktop session. Import `pom.xml` as a Maven project in IntelliJ IDEA and run `ca.utoronto.csc207.ygo.app.Main`. The command-line equivalent is below.

```sh
mvn clean verify
java -jar target/yugioh-deck-manager-0.1.0-SNAPSHOT.jar
```

The default build opens no window and makes no API calls. Add JUnit 5 feature tests under `src/test/java` and keep the live API smoke check separate from the normal test suite. Set up JaCoCo when the team starts collecting coverage.

## MVP ownership

Replace Member A, B and C with the actual owners before sharing the project README. Each lead owns the interactor, controller, presenter, relevant Swing behavior and tests for their slice.

| Use case | Lead | User story |
| --- | --- | --- |
| UC1 Search and inspect a card | Member A | As a player, I want to search by card name and inspect a result so I can identify the card I need. |
| UC2 Track owned copies | Member B | As a collector, I want to record the cards I own and their quantities so I can check my collection later. |
| UC3 Build and save a named deck | Member C | As a player, I want to save named deck lists so I can build and reopen more than one deck. |

Deck analysis and opening-hand draws are outside the MVP. Tournament legality, duel simulation, accounts, cloud sync and import/export are also outside the MVP.

## Use case specifications

### UC1 Search and inspect a card

- **Actor and precondition.** A player has opened the Catalog tab.
- **Trigger.** The player enters a card name and clicks Search.
- **Main flow.** The app shows up to 25 matches from the catalog. The player selects a row. The app displays the card details, a cached thumbnail or placeholder, and makes the selected card available to Collection and Decks.
- **Success outcome.** The selected card can be inspected and used by the other tabs.
- **Extensions.** Blank queries ask for a name. A confirmed no-match shows an empty result. An API failure displays an error or clearly labelled cached results. A thumbnail failure does not block selection.

### UC2 Track owned copies

- **Actor and precondition.** A collector has the Collection tab open. Add requires a catalog selection, while Set and Remove target an existing collection row.
- **Trigger.** The collector adds, changes or removes an owned quantity.
- **Main flow.** The app validates the action, updates a candidate collection and persists it. It displays the new snapshot only after the save succeeds. Add increments an existing count, Set replaces it, and Remove deletes the entry.
- **Success outcome.** The new count is visible and survives restart.
- **Extensions.** Missing selection, invalid quantity, a missing row or storage failure preserves the previously saved collection.

### UC3 Build and save a named deck

- **Actor and precondition.** A player has opened Decks. Adding a card requires an active draft and a catalog selection.
- **Trigger.** The player creates a draft or opens a saved deck.
- **Main flow.** The player names the draft and edits the quantities, then clicks Save. The app validates the name, saves by stable deck UUID, and updates the saved list and total copies.
- **Success outcome.** The player can reopen the deck and its planned counts after restart. Other decks and owned counts are independent.
- **Extensions.** Invalid inputs leave the previous saved deck intact. A write failure keeps the draft available for retry. The view prompts before discarding unsaved edits.

## Shared entities and contracts

All packages below are under `ca.utoronto.csc207.ygo`.

| Type | Shared fields and behavior |
| --- | --- |
| `Card` | Positive provider ID, name, type, description, optional ATK, DEF and level. Identity in maps uses the provider ID. |
| `CollectionEntry` | Card snapshot and owned quantity. `withQuantity` creates a replacement. |
| `DeckEntry` | Card snapshot and planned quantity. `withQuantity` creates a replacement. |
| `Deck` | Stable UUID, trimmed nonblank name and one entry per card ID. `withEntry`, `withoutEntry` and `withName` create new snapshots. `totalCopies` sums planned copies. |

Quantities are 1–999 per entry. Empty decks are valid. For CREATE, use an initial draft name such as `Untitled Deck` and a fresh UUID. The raw name field is passed separately on SAVE so the interactor can reject a blank name without discarding the existing draft.

| Package | Contract |
| --- | --- |
| `use_case.search` | `SearchInputBoundary`, `SearchOutputBoundary`, `SearchRequest`, `SearchResult`, `CardCatalog.search(query, limit)`. The result contains immutable rows and a cache flag so the presenter can label offline results. |
| `use_case.collection` | `CollectionInputBoundary`, `CollectionOutputBoundary`, `CollectionRequest`, `CollectionResult`, `CollectionStore.load/save`. Use LOAD, ADD, SET and REMOVE. |
| `use_case.deck` | `DeckInputBoundary`, `DeckOutputBoundary`, `DeckRequest`, `DeckResult`, `DeckStore.loadAll/saveAll`. Use LIST, OPEN, CREATE, ADD, SET, REMOVE and SAVE. |
| `interface_adapter` | `SelectedCardModel` is the shared catalog selection. The catalog publishes it on the Event Dispatch Thread. Collection and Decks capture it when Add is clicked. |

Input boundaries accept request data. Interactors report one result or one error through the output boundary. File and API adapters translate expected failures to `DataAccessException`. Put HTTP and file I/O off the Swing Event Dispatch Thread and publish view-model updates back on that thread. Use `app.Main` as the composition root for wiring concrete classes. Keep Swing, JSON and HTTP types out of the entity and use case packages.

For DeckResult, LIST sends the saved map and leaves the draft unchanged. OPEN, CREATE and edit actions send the active draft and leave the saved map unchanged. SAVE sends the committed draft and refreshed saved map. An empty saved map is a real result and clears the saved list.

## External API

[YGOPRODeck API v7 documentation](https://ygoprodeck.com/api-guide/)

The catalog calls `GET https://db.ygoprodeck.com/api/v7/cardinfo.php` with an encoded `fname`, `num=25` and `offset=0`. Parse `id`, `name`, `type` and `desc`, and keep missing combat stats optional. Metadata caching and local thumbnails are planned.

A confirmed no-match shows an empty result. Network and API errors display an error message.

## Persistence

Use UTF-8 JSON in a configurable data directory with a default of `~/.csc207-ygo/`. Use separate files and persist enough card metadata for offline display.

```text
collection.json  { schemaVersion: 1, entries: [{ card, quantity }] }
decks.json       { schemaVersion: 1, decks: [{ id, name, entries }] }
card             { id, name, type, description, atk?, def?, level? }
```

A missing file loads as empty. Malformed or unsupported data is a load error. Save via a temporary sibling file and safe replacement. A failed save preserves the previous file. Use a separate cache for API metadata and images.

## Development handoff

1. Confirm the repository and the three named leads. Merge the shared contracts first.
2. A builds the Catalog slice with a fake catalog and then a live HTTP adapter. B builds the Collection slice with a fake store and then a JSON store. C builds the Decks slice with a fake store and then a JSON store. Each lead wires their own panel.
3. Review in a ring. B reviews A, C reviews B, and A reviews C. Keep ownership, run instructions and API status current as PRs merge.

| Checkpoint | Course expectation |
| --- | --- |
| Nov 13 | Shared repository, README blueprint, core entities and initial UI/interactor progress. |
| Nov 20 | Each member submits a substantial PR and reviews a teammate's PR. Each has runnable use case code. The team has a runnable API call and at least one complete use case. |
| Nov 27 | All MVP flows integrated and at least one unit test per interactor. |
| Dec 4 | 100% line coverage for the Entity and Use Case layers, with the demo ready to present. |
| Dec 7 or 8 | Team presentation in lab. Check the posted time for the team. |

Lab 2 supports Git collaboration, Lab 3 supports Swing and JSON, Lab 4 supports stories and UML, Lab 5 supports Java API integration, and Lab 7 covers the project blueprint and UI sketch.

[Project Expectations](https://q.utoronto.ca/courses/444184/pages/project-requirements) · [README and MVP](https://q.utoronto.ca/courses/444184/pages/project-readme-and-minimum-viable-product-mvp) · [Project Timeline](https://q.utoronto.ca/courses/444184/pages/project-timeline-2)
