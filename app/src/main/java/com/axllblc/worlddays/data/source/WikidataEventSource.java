package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.WorldDaysApplication;
import com.axllblc.worlddays.data.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WikidataEventSource implements ReadableEventSource {
    private static final String API_ENDPOINT = "https://query.wikidata.org/sparql";

    // SELECT

    /**
     * <i>Select</i> clause.
     * <p>
     * <b>Selected columns:</b>
     * <dl>
     *     <dt>?worldDay</dt><dd>Wikidata item URL</dd>
     *     <dt>?worldDayLabel</dt><dd>Wikidata item label</dd>
     *     <dt>?month</dt><dd>Month [1-12]</dd>
     *     <dt>?dayOfMonth</dt><dd>Day of month [1-31]</dd>
     * </dl>
     */
    private static final String SPARQL_SELECT =
            "?worldDay ?worldDayLabel ?month ?dayOfMonth";
    /**
     * <i>Select</i> clause.
     * <p>
     * <b>Selected columns:</b>
     * <dl>
     *     <dt>?article</dt><dd>Wikipedia article URL</dd>
     *     <dt>?inception</dt><dd>Date of creation</dd>
     *     <dt>?founderLabel</dt><dd>Founder</dd>
     * </dl>
     */
    private static final String SPARQL_SELECT_DETAILED =
            "?article ?inception ?founderLabel";

    // WHERE

    /**
     * <i>Where</i> statements.
     *
     * @see #SPARQL_WHERE_DETAILED
     */
    private static final String SPARQL_WHERE =
            "?worldDay wdt:P31/wdt:P279* wd:Q2558684. " +                      // ?worldDay is a world day
            "?worldDay wdt:P837 ?_dayOfYear. " +                               // ?worldDay occurs every ?_dayOfYear
            "?_dayOfYear p:P361 [ps:P361 ?_month; pq:P1545 ?dayOfMonth]. " +   // ?_dayOfYear is the ?dayOfMonth-th day of ?_month
            "?_month p:P279 [ps:P279 wd:Q18602249; pq:P1545 ?month]. ";        // ?_month is the ?month-th month of Gregorian calendar

    /**
     * <i>Where</i> statements for detailed results. Argument: language code.
     *
     * @see #SPARQL_WHERE
     */
    private static final String SPARQL_WHERE_DETAILED =
            "OPTIONAL { ?worldDay wdt:P571 ?inception. } " +
            "OPTIONAL { ?worldDay wdt:P112 ?founder. " +
                    " ?founder rdfs:label ?founderLabel filter (lang(?founderLabel) = \"%s\"). " +
            "} ";

    /**
     * World day label. Argument: language code.
     */
    private static final String SPARQL_WORLD_DAY_LABEL =
            "?worldDay rdfs:label ?worldDayLabel filter (lang(?worldDayLabel) = \"%s\"). ";

    /**
     * Wikipedia URL. Argument: language code.
     */
    private static final String SPARQL_WIKIPEDIA_URL =
            "OPTIONAL { " +
                    "?article schema:about ?worldDay. " +
                    "?article schema:inLanguage \"%s\". " +
            "}";

    /**
     * Filter by label. Arguments: language code, string to search.
     */
    private static final String SPARQL_LABEL_FILTER =
            "?worldDay rdfs:label ?worldDayLabel " +
                    "filter (lang(?worldDayLabel) = \"%s\" && " +
                        "CONTAINS(LCASE(?worldDayLabel), LCASE(\"%s\"))).";

    // ORDER BY

    /**
     * <i>Order by</i> clause.
     */
    private static final String SPARQL_ORDER_BY =
            "xsd:integer(?month) xsd:integer(?dayOfMonth)";


    private final OkHttpClient client;

    public static WikidataEventSource getInstance(WorldDaysApplication applicationContext) {
        return new WikidataEventSource(applicationContext.getContainer().getOkHttpClient());
    }

    public WikidataEventSource(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Optional<Event> getEvent(String id, boolean withDetails) throws Exception {
        try (Response response = executeQuery(getEventQuery(id, withDetails))) {
            return handleResponse(response, withDetails).stream().findFirst();
        }
    }

    /**
     * Returns the SPARQL query for {@link #getEvent(String, boolean)}.
     */
    static String getEventQuery(String id, boolean withDetails) {
        QueryBuilder qb = new QueryBuilder();
        String language = Locale.getDefault().getLanguage();

        qb.addSelect(SPARQL_SELECT)
                .addWhere("BIND (wd:%s AS ?worldDay).", id)
                .addWhere(SPARQL_WORLD_DAY_LABEL, language)
                .addWhere(SPARQL_WHERE)
                .limit(1);

        if (withDetails) {
            qb.addSelect(SPARQL_SELECT_DETAILED)
                    .addWhere(SPARQL_WHERE_DETAILED, language)
                    .addWhere(SPARQL_WIKIPEDIA_URL, language)
                    .addOrderBy("?inception");
        }

        return qb.build();
    }

    @Override
    public List<Event> getAll() throws Exception {
        try (Response response = executeQuery(getAllQuery())) {
            return handleResponse(response);
        }
    }

    /**
     * Returns the SPARQL query for {@link #getAll()}.
     */
    static String getAllQuery() {
        QueryBuilder qb = new QueryBuilder();
        String language = Locale.getDefault().getLanguage();

        qb.addSelect(SPARQL_SELECT)
                .addWhere(SPARQL_WORLD_DAY_LABEL, language)
                .addWhere(SPARQL_WHERE)
                .addOrderBy(SPARQL_ORDER_BY);

        return qb.build();
    }

    @Override
    public List<Event> getEventsByName(String str) throws Exception {
        try (Response response = executeQuery(getEventsByNameQuery(str))) {
            return handleResponse(response);
        }
    }

    /**
     * Returns the SPARQL query for {@link #getEventsByName(String)}.
     */
    static String getEventsByNameQuery(String str) {
        QueryBuilder qb = new QueryBuilder();
        String language = Locale.getDefault().getLanguage();

        qb.addSelect(SPARQL_SELECT)
                .addWhere(SPARQL_WHERE)
                .addWhere(SPARQL_LABEL_FILTER, language, str.replace("\"", "\\\""))
                .addOrderBy(SPARQL_ORDER_BY);

        return qb.build();
    }

    @Override
    public List<Event> getEventsByMonth(int month) throws Exception {
        try (Response response = executeQuery(getEventsByMonthQuery(month))) {
            return handleResponse(response);
        }
    }

    /**
     * Returns the SPARQL query for {@link #getEventsByMonth(int)}.
     */
    static String getEventsByMonthQuery(int month) {
        QueryBuilder qb = new QueryBuilder();
        String language = Locale.getDefault().getLanguage();

        qb.addSelect(SPARQL_SELECT)
                .addWhere(SPARQL_WORLD_DAY_LABEL, language)
                .addWhere("BIND (\"%d\" AS ?month).", month)
                .addWhere(SPARQL_WHERE)
                .addOrderBy(SPARQL_ORDER_BY);

        return qb.build();
    }

    /**
     * Executes the SPARQL query, using {@link #client}, and returns a {@link Response} object.
     * @param query SPARQL query, as string
     * @return {@link Response} object
     * @throws IOException If the request failed
     */
    private Response executeQuery(String query) throws IOException {
        // Build the URL
        //noinspection DataFlowIssue
        HttpUrl url = HttpUrl.parse(API_ENDPOINT).newBuilder()
                .addQueryParameter("query", query)
                .build();

        // Build the request
        Request get = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", WorldDaysApplication.USER_AGENT)
                .addHeader("Accept", "application/sparql-results+json")
                .build();

        // Execute the request
        return client.newCall(get).execute();
    }

    private List<Event> handleResponse(Response response) throws Exception {
        return handleResponse(response, false);
    }

    /**
     * Builds a {@link List} of {@link Event}s from a {@link Response} object.
     * @param response {@link Response} object
     * @param withDetails {@code true} to create {@code Event} objects with details
     *                    (wikipediaURL, inception, founder)
     * @return {@link List} of {@link Event}s
     */
    private List<Event> handleResponse(Response response, boolean withDetails) throws Exception {
        if (response.isSuccessful()) {
            List<Event> events = new ArrayList<>();
            String body = response.body().string();

            JSONArray bindings = new JSONObject(body)
                    .getJSONObject("results")
                    .getJSONArray("bindings");

            for (int i = 0; i < bindings.length(); i++) {
                events.add(jsonToEvent(bindings.getJSONObject(i), withDetails));
            }

            return events;
        } else throw new NotSuccessfulResponseException(response.toString());
    }

    /**
     * Creates an {@link Event} from a JSON object returned by Wikidata Query Service.
     * @param object JSON Object
     * @param withDetails {@code true} to create an {@code Event} object with details
     *                    (wikipediaURL, inception, founder)
     * @return An {@link Event}
     * @throws JSONException If parsing failed
     */
    private Event jsonToEvent(JSONObject object, boolean withDetails) throws JSONException {
        String id = getValueInJsonObject(object, "worldDay")
                .replaceFirst("^.+/", "");
        // Example : http://www.wikidata.org/entity/Q5305947 → Q5305947

        String title = getValueInJsonObject(object, "worldDayLabel");

        int month = Integer.parseInt(getValueInJsonObject(object, "month"));
        int dayOfMonth = Integer.parseInt(getValueInJsonObject(object, "dayOfMonth"));
        MonthDay monthDay = MonthDay.of(month, dayOfMonth);

        if (withDetails) {
            String article = getNullableValueInJsonObject(object, "article");

            String inceptionString = getNullableValueInJsonObject(object, "inception");
            LocalDate inception = null;
            if (inceptionString != null)
                inception = LocalDate.parse(inceptionString
                        .replaceFirst("T.+$", "")  // Remove time & zone
                );

            String founder = getNullableValueInJsonObject(object, "founder");

            return new Event(id, title, monthDay, article, null, inception, founder);
        } else /* if (!withDetails) */ {
            return new Event(id, title, monthDay);
        }
    }

    private String getValueInJsonObject(JSONObject object, String name) throws JSONException {
        return object.getJSONObject(name).getString("value");
    }

    private String getNullableValueInJsonObject(JSONObject object, String name) throws JSONException {
        return object.has(name) ? getValueInJsonObject(object, name) : null;
    }


    /**
     * Simple SPARQL query builder.
     */
    static class QueryBuilder {
        /**
         * List of selected columns.
         */
        private final List<String> select = new ArrayList<>();
        private final List<String> where = new ArrayList<>();
        private final List<String> orderBy = new ArrayList<>();
        private Integer limit = null;

        public QueryBuilder addSelect(String select) {
            this.select.add(select);
            return this;
        }

        public QueryBuilder addWhere(String where) {
            this.where.add(where);
            return this;
        }

        public QueryBuilder addWhere(String where, Object... args) {
            return addWhere(String.format(where, args));
        }

        public QueryBuilder addOrderBy(String orderBy) {
            this.orderBy.add(orderBy);
            return this;
        }

        public QueryBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Build SPARQL query
         * @return SPARQL query, as {@link String}
         */
        public String build() {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ").append(String.join(" ", select))
                    .append(" WHERE {").append(String.join(" ", where)).append("}");
            if (!orderBy.isEmpty())
                sb.append(" ORDER BY ").append(String.join(" ", orderBy));
            if (limit != null)
                sb.append(" LIMIT ").append(limit);
            return sb.toString();
        }
    }
}
