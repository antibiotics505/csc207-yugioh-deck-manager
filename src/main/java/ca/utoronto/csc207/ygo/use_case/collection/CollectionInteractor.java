package ca.utoronto.csc207.ygo.use_case.collection;
import ca.utoronto.csc207.ygo.entity.Quantity;
import ca.utoronto.csc207.ygo.entity.CollectionEntry;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionInteractor implements CollectionInputBoundary {
    private final CollectionStore store;
    private final CollectionOutputBoundary output;

    public CollectionInteractor(CollectionStore store, CollectionOutputBoundary output) {
        this.store = store;
        this.output = output;
    }
    @Override
    public void execute(CollectionRequest request) {
        if (request.action() == CollectionRequest.Action.LOAD) {
            loadCollection();
            return;
        }
        if (request.action() == CollectionRequest.Action.SET) {
            setQuantity(request.cardId(), request.quantity());
            return;
        }
        if (request.action() == CollectionRequest.Action.REMOVE) {
            removeCard(request.cardId());
            return;
        }
        if (request.action() != CollectionRequest.Action.ADD) {
            return;
        }

        if (request.card() == null) {
            output.presentError("Select a catalog card first.");
            return;
        }

        int quantity = request.quantity();
        if (quantity < Quantity.MIN || quantity > Quantity.MAX) {
            output.presentError("Quantity must be between 1 and 999.");
            return;
        }
        try {
            Map<Long, CollectionEntry> entries = new LinkedHashMap<>(store.load());
            long cardId = request.card().id();

            CollectionEntry existing = entries.get(cardId);
            int oldQuantity = 0;
            if (existing != null) {
                oldQuantity = existing.quantity();
            }

            int newQuantity = oldQuantity + quantity;
            if (newQuantity > Quantity.MAX) {
                output.presentError("Total owned copies cannot exceed 999.");
                return;
            }

            entries.put(cardId, new CollectionEntry(request.card(), newQuantity));
            store.save(entries);
            output.present(new CollectionResult(entries));
        } catch (DataAccessException e) {
            output.presentError("Could not read or save the collection.");
        }
    }
    private void loadCollection() {
        try {
            output.present(new CollectionResult(store.load()));
        } catch (DataAccessException e) {
            output.presentError("Could not load the collection.");
        }
    }
    private void setQuantity(long cardId, int quantity) {
        if (quantity < Quantity.MIN || quantity > Quantity.MAX) {
            output.presentError("Quantity must be between 1 and 999.");
            return;
        }

        try {
            Map<Long, CollectionEntry> entries = new LinkedHashMap<>(store.load());
            CollectionEntry existing = entries.get(cardId);
            if (existing == null) {
                output.presentError("Card is not in the collection.");
                return;
            }

            entries.put(cardId, existing.withQuantity(quantity));
            store.save(entries);
            output.present(new CollectionResult(entries));
        } catch (DataAccessException e) {
            output.presentError("Could not read or save the collection.");
        }
    }
    private void removeCard(long cardId) {
        try {
            Map<Long, CollectionEntry> entries = new LinkedHashMap<>(store.load());
            if (entries.remove(cardId) == null) {
                output.presentError("Card is not in the collection.");
                return;
            }

            store.save(entries);
            output.present(new CollectionResult(entries));
        } catch (DataAccessException e) {
            output.presentError("Could not read or save the collection.");
        }
    }
}
