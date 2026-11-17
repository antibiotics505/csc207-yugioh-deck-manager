package ca.utoronto.csc207.ygo.app;

import ca.utoronto.csc207.ygo.data_access.DemoCardCatalog;
import ca.utoronto.csc207.ygo.use_case.search.SearchInteractor;
import ca.utoronto.csc207.ygo.interface_adapter.SelectedCardModel;
import ca.utoronto.csc207.ygo.view.CatalogPanel;
import ca.utoronto.csc207.ygo.view.CollectionPanel;
import ca.utoronto.csc207.ygo.view.DeckPanel;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Yu-Gi-Oh! Collection and Deck Manager");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(createTabs());
            window.setPreferredSize(new Dimension(820, 480));
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    static JTabbedPane createTabs() {
        SelectedCardModel selection = new SelectedCardModel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        CatalogPanel catalog = new CatalogPanel(selection);
        catalog.connectSearch(new SearchInteractor(new DemoCardCatalog(), catalog));
        tabs.addTab("Catalog (offline demo)", catalog);
        tabs.addTab("Collection", new CollectionPanel(selection));
        tabs.addTab("Decks", new DeckPanel(selection));
        return tabs;
    }
}
