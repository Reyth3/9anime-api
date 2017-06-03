package com.ruto.nineanimeandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ruto.nineanimeandroid.api.data.Anime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ruto on 2/23/2017.
 */

public class AnimeFavorites {
    private static final String PREFERENCE_FAVORITES = "com.ruto.9anime.FAVORITES";
    private static final String KEY_CODES = "codes";

    private List<Anime> favorites = new ArrayList<>();
    private SharedPreferences sharedPref;
    private boolean loaded = false;

    public AnimeFavorites(Context ctx) {
        sharedPref = ctx.getSharedPreferences(PREFERENCE_FAVORITES, Context.MODE_PRIVATE);
    }

    public List<Anime> getFavorites() {
        if (!loaded) {
            load();
        }
        return favorites;
    }

    public void add(Anime anime) {
        if (favorites.contains(anime)) return;
        favorites.add(anime);
        save();
    }

    public void remove(Anime anime) {
        if (!favorites.contains(anime)) return;
        favorites.remove(anime);
        save();
    }

    private void save() {
        SharedPreferences.Editor editPref = sharedPref.edit();
        Set<String> codes = new HashSet<String>();
        for (Anime anime : favorites) {
            codes.add(anime.getId());
            editPref.putString(anime.getId(), anime.toJson());
        }
        editPref.putStringSet(KEY_CODES, codes);
        editPref.apply();
    }

    public void load() {
        loaded = true;
        favorites.clear();
        Set<String> codes = sharedPref.getStringSet(KEY_CODES, null);
        if (codes == null) return;
        for (String code : codes) {
            String json = sharedPref.getString(code, null);
            if (json == null) continue;
            favorites.add(Anime.fromJson(json));
        }
    }
}
