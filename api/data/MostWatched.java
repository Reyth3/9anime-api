package com.ruto.nineanimeandroid.api.data;

import com.ruto.nineanimeandroid.Session;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ruto on 2/27/2017.
 */

public class MostWatched {
    private Document doc;
    private int maxPage = 0;

    public MostWatched(Document doc) {
        this.doc = doc;
        maxPage = Integer.parseInt(doc.select("span.total").first().text().trim());
    }

    public int getMaxPages() {
        return maxPage;
    }

    public List<Anime> getPage(int page) throws IOException, InterruptedException, ExecutionException, JSONException {
        return Session.getInstance().getApi().parseSearchPage(page <= 1 ? doc : Jsoup.connect(doc.location()+"?page="+page).get());
    }

}
