package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.WorldDaysApplication;
import com.axllblc.worlddays.data.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class that fetches the introduction of Wikipedia articles.
 * 
 * @see #getArticleIntro(String) 
 */
public class WikipediaIntroSourceImpl implements WikipediaIntroSource {
    private static final String API_ENDPOINT = "https://%s.wikipedia.org/w/api.php";

    private final OkHttpClient client;

    @Inject
    public WikipediaIntroSourceImpl(OkHttpClient client) {
        this.client = client;
    }

    /**
     * Fetches the introduction of the given Wikipedia article.
     * @param articleURL Wikipedia article URL, such as {@code https://en.wikipedia.org/wiki/International_Day_of_Happiness}
     * @return Introduction, as plain text
     */
    @Override
    public Result<String> getArticleIntro(String articleURL) {
        try {
            Request get = new Request.Builder()
                    .url(getArticleURL(articleURL))
                    .addHeader("User-Agent", WorldDaysApplication.USER_AGENT)
                    .build();

            try (Response response = client.newCall(get).execute()) {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    return Result.success(
                            new JSONObject(body)
                                    .getJSONObject("query")
                                    .getJSONArray("pages")
                                    .getJSONObject(0)
                                    .getString("extract")
                    );
                } else return Result.error(new NotSuccessfulResponseException(response.toString()));
            }
        } catch (IOException | URISyntaxException | JSONException e) {
            return Result.error(e);
        }
    }

    private static HttpUrl getArticleURL(String articleURL) throws URISyntaxException {
        // Extract the wiki ID and the title of the article
        Pattern pattern = Pattern.compile("^https?://(\\w+)\\.wikipedia.org/wiki/(.+)$");
        Matcher matcher = pattern.matcher(articleURL);
        //noinspection ResultOfMethodCallIgnored
        matcher.find();

        String wiki = matcher.group(1);
        String title = matcher.group(2);

        //noinspection DataFlowIssue
        HttpUrl.Builder uriBuilder = HttpUrl.parse(String.format(API_ENDPOINT, wiki)).newBuilder();

        uriBuilder
                .addQueryParameter("action", "query")
                .addQueryParameter("format", "json")
                .addQueryParameter("prop", "extracts")
                .addQueryParameter("formatversion", "2")
                .addQueryParameter("exintro", "1")
                .addQueryParameter("explaintext", "1")
                .addEncodedQueryParameter("titles", title);

        return uriBuilder.build();
    }
}
