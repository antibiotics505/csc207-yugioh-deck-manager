package ca.utoronto.csc207.ygo.use_case.search;

import ca.utoronto.csc207.ygo.use_case.DataAccessException;

public interface CardCatalog {
    /** Returns at most limit cards. An empty live result means a confirmed no-match. */
    SearchResult search(String query, int limit) throws DataAccessException;
}
