package com.ruto.nineanimeandroid;

import android.content.Context;

import com.google.gson.Gson;
import com.ruto.nineanimeandroid.api.NineAnimeApi;
import com.ruto.nineanimeandroid.utils.AnimeFavorites;

import java.io.IOException;

/**
 * Created by Ruto on 2/23/2017.
 */

public class Session {
    private NineAnimeApi nineAnimeApi;
    private AnimeFavorites animeFavorites;

    private Gson gson;

    private static Session instance;
    public static Session getInstance() {
        return instance == null ? (instance = new Session(NineAnimeAndroidApplication.getAppContext())) : instance;
    }

    public Session(Context ctx) {
        try {
            nineAnimeApi = NineAnimeApi.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        animeFavorites = new AnimeFavorites(ctx);
        gson = new Gson();
    }

    public NineAnimeApi getApi() {
        return nineAnimeApi;
    }

    public AnimeFavorites getFavorites() {
        return animeFavorites;
    }

    public Gson getGson() {
        return gson;
    }
}
