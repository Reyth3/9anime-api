package com.ruto.nineanimeandroid.api;

import android.util.Log;
import android.util.SparseArray;

import com.ruto.nineanimeandroid.api.data.Anime;
import com.ruto.nineanimeandroid.api.data.ContentFilter;
import com.ruto.nineanimeandroid.api.data.MostWatched;

import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ruto on 2/22/2017.
 */

public class NineAnimeApi {
    private static final String TAG = NineAnimeApi.class.getSimpleName();
    private static final String HOMEPAGE_URL = "https://9anime.to";
    private static final String[] filterFields = {"updated", "updated-sub", "updated-dub", "top-week"};

    private Document homepage;
    private ContentFilter frontpageFilter;
    private SparseArray<List<Anime>> frontpageCache = new SparseArray<>();
    private MostWatched mostWatched;

    private static NineAnimeApi instance;
    public static NineAnimeApi getInstance() throws IOException {
        return instance == null ? (instance = new NineAnimeApi()) : instance;
    }

    private NineAnimeApi() throws IOException {
        frontpageFilter = ContentFilter.RecentlyUpdated;
        Connection con = Jsoup.connect(HOMEPAGE_URL);
        homepage = con.execute().parse();
        Log.d(TAG, "9Anime homepage parsed");
    }

    public NineAnimeApi refresh() throws IOException {
        instance = null;
        Log.d(TAG, "Recreating 9AnimeApi instance");
        return getInstance();
    }

    public void setFrontpageFilter(ContentFilter filter) {
        frontpageFilter = filter;
    }

    public List<Anime> getFrontpage() {
        int index = frontpageFilter.ordinal();
        List<Anime> animes;
        if ((animes = frontpageCache.get(index)) != null)
            return animes;
        animes = new ArrayList<>();
        Element page = homepage.select("div.list-film").first().select("div[data-name='" + filterFields[index] + "']").first();
        for (Element item : page.select("div.item")) {
            Element image = item.select("img").first();
            Element name = item.select("a.name").first();
            animes.add(new Anime(name.text().trim(), name.attr("href"), image.attr("src"), HOMEPAGE_URL + "/" + item.select("a.poster").first().attr("data-tip")));
        }
        frontpageCache.put(index, animes);
        Log.d(TAG, "Cached category[\"" + filterFields[index] + "\"]");
        return animes;
    }

    public MostWatched getMostWatched() throws IOException {
        return mostWatched == null ? (mostWatched = new MostWatched(Jsoup.connect(HOMEPAGE_URL + "/most-watched").get())) : mostWatched;
    }

    public List<Anime> search(String query) throws IOException, InterruptedException, ExecutionException, JSONException {
        String url = "https://9anime.to/search?keyword=" + query.replace(" ", "+");
        Document doc = Jsoup.connect(url).get();
        int totalPages = 1;

        List<Anime> animes = new ArrayList<>();

        if (doc.select("div.paging").size() > 0) {
            totalPages = Math.max(Integer.parseInt(doc.select("span.total").first().text()), totalPages);
        }

        animes.addAll(parseSearchPage(doc));

        for (int i = 1; i < totalPages; ++i) {
            animes.addAll(parseSearchPage(Jsoup.connect(url + "&page=" + (i + 1)).get()));
        }

        return animes;
    }

    public List<Anime> parseSearchPage(Document src) throws IOException, InterruptedException, ExecutionException, JSONException {
        List<Anime> animes = new ArrayList<>();
        Elements els = src.select("div.list-film").select("div.item");
        for (Element item : els) {
            Element image = item.select("img").first();
            Element name = item.select("a.name").first();
            animes.add(new Anime(name.text().trim(), name.attr("href"), image.attr("src"), HOMEPAGE_URL + "/" + item.select("a.poster").first().attr("data-tip")));
        }
        return animes;
    }

    public Anime random() throws IOException {
        String randomUrl = "https://9anime.to/random";
        Document doc = Jsoup.connect(randomUrl).get();
        String title = doc.select("h1.title").text();
        Element info = doc.select("div[id='info']").first();
        Element image = info.select("img").first();
        return new Anime(title, doc.location(), image.attr("src"));
    }

}
