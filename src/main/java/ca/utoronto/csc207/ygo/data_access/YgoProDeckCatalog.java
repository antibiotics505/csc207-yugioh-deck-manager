package ca.utoronto.csc207.ygo.data_access;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;
import ca.utoronto.csc207.ygo.use_case.search.CardCatalog;
import ca.utoronto.csc207.ygo.use_case.search.SearchResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class YgoProDeckCatalog implements CardCatalog {
    private static final String ENDPOINT = "https://db.ygoprodeck.com/api/v7/cardinfo.php";
    private static final String NO_MATCH = "No card matching your query was found in the database.";
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public SearchResult search(String query, int limit) throws DataAccessException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        URI uri = URI.create(ENDPOINT + "?fname=" + encodedQuery + "&num=" + limit + "&offset=0");
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            return parse(client.send(request, HttpResponse.BodyHandlers.ofString()), limit);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DataAccessException("Card search was interrupted.", exception);
        } catch (IOException | JSONException | IllegalArgumentException exception) {
            throw new DataAccessException("Could not read the catalog response.", exception);
        }
    }

    private static SearchResult parse(HttpResponse<String> response, int limit) throws DataAccessException {
        int status = response.statusCode();
        if (status == 400 && new JSONObject(response.body()).optString("error").startsWith(NO_MATCH)) {
            return new SearchResult(List.of(), false);
        }
        if (status != 200) {
            throw new DataAccessException("YGOPRODeck returned HTTP " + status + ".");
        }
        JSONArray data = new JSONObject(response.body()).getJSONArray("data");
        List<Card> cards = new ArrayList<>();
        for (int index = 0; index < Math.min(data.length(), limit); index++) {
            JSONObject item = data.getJSONObject(index);
            cards.add(new Card(item.getLong("id"), item.getString("name"),
                    item.getString("type"), item.getString("desc"),
                    optionalInt(item, "atk"), optionalInt(item, "def"), optionalInt(item, "level")));
        }
        return new SearchResult(cards, false);
    }

    private static Integer optionalInt(JSONObject item, String key) {
        return item.isNull(key) ? null : item.getInt(key);
    }
}
