package ca.utoronto.csc207.ygo.view;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.entity.DeckEntry;
import ca.utoronto.csc207.ygo.entity.Quantity;
import ca.utoronto.csc207.ygo.interface_adapter.DeckController;
import ca.utoronto.csc207.ygo.interface_adapter.DeckViewModel;
import ca.utoronto.csc207.ygo.interface_adapter.SelectedCardModel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DeckPanel extends JPanel {
    private final SelectedCardModel selection;
    private final DeckViewModel viewModel;
    private final DeckController controller;

    private final JComboBox<DeckOption> savedDecks = new JComboBox<>();
    private final JTextField deckName = new JTextField(18);
    private final JSpinner quantity =
            new JSpinner(new SpinnerNumberModel(1, Quantity.MIN, Quantity.MAX, 1));
    private final JLabel selectedCardLabel = new JLabel();
    private final JLabel status = new JLabel("No deck open");
    private final DeckTableModel tableModel = new DeckTableModel();
    private final JTable entries = new JTable(tableModel);

    public DeckPanel(
            SelectedCardModel selection,
            DeckViewModel viewModel,
            DeckController controller) {
        super(new BorderLayout(8, 8));
        this.selection = selection;
        this.viewModel = viewModel;
        this.controller = controller;

        JPanel controls = new JPanel(new GridLayout(0, 1));

        JPanel deckControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        deckControls.add(new JLabel("Saved decks"));
        deckControls.add(savedDecks);

        JButton newDeck = new JButton("New");
        deckControls.add(newDeck);

        deckControls.add(new JLabel("Deck name"));
        deckControls.add(deckName);

        JButton save = new JButton("Save");
        deckControls.add(save);
        controls.add(deckControls);

        JPanel entryControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        selectedCardLabel.setText(selectionText());
        entryControls.add(selectedCardLabel);
        entryControls.add(new JLabel("Quantity"));
        entryControls.add(quantity);

        JButton add = new JButton("Add");
        JButton set = new JButton("Set");
        JButton remove = new JButton("Remove");
        entryControls.add(add);
        entryControls.add(set);
        entryControls.add(remove);
        controls.add(entryControls);

        add(controls, BorderLayout.NORTH);
        add(new JScrollPane(entries), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        newDeck.addActionListener(event -> controller.createDeck());

        savedDecks.addActionListener(event -> {
            DeckOption option = (DeckOption) savedDecks.getSelectedItem();
            if (option != null) {
                controller.openDeck(option.id());
            }
        });

        save.addActionListener(event -> viewModel.currentDraft().ifPresent(
                draft -> controller.saveDeck(draft, deckName.getText())));

        add.addActionListener(event -> {
            Card card = selection.selectedCard().orElse(null);
            if (card == null) {
                showError("Select a card from the catalog first.");
                return;
            }

            viewModel.currentDraft().ifPresent(draft ->
                    controller.addCard(draft, card, (Integer) quantity.getValue()));
        });

        set.addActionListener(event -> {
            int row = entries.getSelectedRow();
            if (row < 0) {
                showError("Select a card in the deck table first.");
                return;
            }

            viewModel.currentDraft().ifPresent(draft -> {
                DeckEntry entry = tableModel.entryAt(row);
                controller.setCardQuantity(
                        draft, entry.card().id(), (Integer) quantity.getValue());
            });
        });

        remove.addActionListener(event -> {
            int row = entries.getSelectedRow();
            if (row < 0) {
                showError("Select a card in the deck table first.");
                return;
            }

            viewModel.currentDraft().ifPresent(draft -> {
                DeckEntry entry = tableModel.entryAt(row);
                controller.removeCard(draft, entry.card().id());
            });
        });

        selection.addSelectionListener(event ->
                selectedCardLabel.setText(selectionText()));

        viewModel.addPropertyChangeListener(this::viewModelChanged);

        // 启动时读取已保存卡组。
        controller.listDecks();
    }

    private void viewModelChanged(PropertyChangeEvent event) {
        Runnable update = () -> {
            switch (event.getPropertyName()) {
                case "currentDraft":
                    renderDraft();
                    break;
                case "savedDecks":
                    renderSavedDecks();
                    break;
                case "errorMessage":
                    viewModel.errorMessage().ifPresent(this::showError);
                    break;
                default:
                    break;
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    private void renderDraft() {
        Deck draft = viewModel.currentDraft().orElse(null);
        if (draft == null) {
            deckName.setText("");
            tableModel.setEntries(List.of());
            status.setText("No deck open");
            return;
        }

        deckName.setText(draft.name());
        tableModel.setEntries(new ArrayList<>(draft.entries().values()));
        status.setText("Total copies: " + draft.totalCopies());
    }

    private void renderSavedDecks() {
        UUID previouslySelected = null;
        DeckOption selected = (DeckOption) savedDecks.getSelectedItem();
        if (selected != null) {
            previouslySelected = selected.id();
        }

        savedDecks.removeAllItems();

        UUID draftId = viewModel.currentDraft()
                .map(Deck::id)
                .orElse(previouslySelected);

        for (Map.Entry<UUID, Deck> entry : viewModel.savedDecks().entrySet()) {
            DeckOption option = new DeckOption(entry.getKey(), entry.getValue().name());
            savedDecks.addItem(option);

            if (entry.getKey().equals(draftId)) {
                savedDecks.setSelectedItem(option);
            }
        }
    }

    private String selectionText() {
        return selection.selectedCard()
                .map(Card::name)
                .orElse("No catalog card selected");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Deck Manager", JOptionPane.ERROR_MESSAGE);
    }

    private static final class DeckOption {
        private final UUID id;
        private final String name;

        private DeckOption(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        private UUID id() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final class DeckTableModel extends AbstractTableModel {
        private final String[] columns = {"Card", "Planned copies"};
        private List<DeckEntry> rows = new ArrayList<>();

        private void setEntries(List<DeckEntry> entries) {
            rows = new ArrayList<>(entries);
            fireTableDataChanged();
        }

        private DeckEntry entryAt(int row) {
            return rows.get(row);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            DeckEntry entry = rows.get(row);
            if (column == 0) {
                return entry.card().name();
            }
            return entry.quantity();
        }
    }
}