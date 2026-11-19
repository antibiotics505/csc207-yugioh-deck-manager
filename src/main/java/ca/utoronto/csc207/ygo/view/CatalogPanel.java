package ca.utoronto.csc207.ygo.view;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.interface_adapter.SelectedCardModel;
import ca.utoronto.csc207.ygo.use_case.search.SearchInputBoundary;
import ca.utoronto.csc207.ygo.use_case.search.SearchOutputBoundary;
import ca.utoronto.csc207.ygo.use_case.search.SearchRequest;
import ca.utoronto.csc207.ygo.use_case.search.SearchResult;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.util.OptionalInt;

public final class CatalogPanel extends JPanel implements SearchOutputBoundary {
    private final JTextField query = new JTextField();
    private final JButton search = new JButton("Search");
    private final DefaultListModel<Card> cards = new DefaultListModel<>();
    private final JTextArea details = new JTextArea();
    private final JLabel status = new JLabel("Enter a card name to search.");

    public CatalogPanel(SelectedCardModel selection) {
        super(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        JLabel queryLabel = new JLabel("Card name");
        queryLabel.setLabelFor(query);
        searchBar.add(queryLabel, BorderLayout.WEST);
        searchBar.add(query, BorderLayout.CENTER);
        searchBar.add(search, BorderLayout.EAST);
        add(searchBar, BorderLayout.NORTH);

        JList<Card> results = new JList<>(cards);
        results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        results.setCellRenderer((list, card, index, selected, focused) ->
                renderer.getListCellRendererComponent(list, card.name(), index, selected, focused));
        results.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                Card card = results.getSelectedValue();
                selection.setSelectedCard(card);
                details.setText(card == null ? "" : cardDetails(card));
                details.setCaretPosition(0);
            }
        });
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(results), new JScrollPane(details));
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    public void connectSearch(SearchInputBoundary input) {
        search.addActionListener(event -> {
            SearchRequest request = new SearchRequest(query.getText());
            search.setEnabled(false);
            status.setText("Searching YGOPRODeck…");
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    input.execute(request);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        presentError("Card search was interrupted.");
                    } catch (java.util.concurrent.ExecutionException exception) {
                        presentError("Could not complete the card search.");
                    } finally {
                        search.setEnabled(true);
                    }
                }
            }.execute();
        });
        query.addActionListener(event -> search.doClick());
    }

    @Override
    public void present(SearchResult result) {
        onEdt(() -> {
            cards.clear();
            result.cards().forEach(cards::addElement);
            status.setText(cards.isEmpty() ? "No matching cards."
                    : cards.size() + " match(es). Select a card.");
        });
    }

    @Override
    public void presentError(String message) {
        onEdt(() -> {
            cards.clear();
            status.setText(message);
        });
    }

    private static void onEdt(Runnable update) {
        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    private static String cardDetails(Card card) {
        return card.name() + "\n" + card.type()
                + "\nATK " + displayStat(card.attack())
                + "   DEF " + displayStat(card.defense())
                + "   Level " + displayStat(card.level())
                + "\n\n" + card.description();
    }

    private static String displayStat(OptionalInt value) {
        return value.isPresent() ? Integer.toString(value.getAsInt()) : "—";
    }
}
