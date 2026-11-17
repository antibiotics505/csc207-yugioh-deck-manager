package ca.utoronto.csc207.ygo.app;

import ca.utoronto.csc207.ygo.data_access.DemoCardCatalog;
import ca.utoronto.csc207.ygo.data_access.YgoProDeckCatalog;
import ca.utoronto.csc207.ygo.use_case.search.CardCatalog;
import java.util.Arrays;
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
        boolean offline = Arrays.asList(args).contains("--offline-demo");
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Yu-Gi-Oh! Collection and Deck Manager");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(createTabs(offline));
            window.setPreferredSize(new Dimension(820, 480));
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    static JTabbedPane createTabs(boolean offline) {
        SelectedCardModel selection = new SelectedCardModel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        CatalogPanel catalog = new CatalogPanel(selection);
        CardCatalog source = offline ? new DemoCardCatalog() : new YgoProDeckCatalog();
        catalog.connectSearch(new SearchInteractor(source, catalog), !offline);
        tabs.addTab(offline ? "Catalog (offline demo)" : "Catalog", catalog);
        tabs.addTab("Collection", new CollectionPanel(selection));
        tabs.addTab("Decks", new DeckPanel(selection));
        return tabs;
    }
}
