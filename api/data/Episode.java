package com.ruto.nineanimeandroid.api.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.ruto.nineanimeandroid.utils.DownloadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ruto on 2/23/2017.
 */

public class Episode implements Parcelable {
    private static final String GRABBER_API = "https://9anime.to/ajax/episode/info?update=0&film=%s&id=%s";

    private String animeId;
    private String episode;
    private String link;
    private List<Video> medias;

    public Episode(String animeId, String episode, String link) {
        this.animeId = animeId;
        this.episode = episode;
        this.link = link;
    }

    public String getEpisode() {
        return episode;
    }

    public String getAnimeId() {
        return animeId;
    }

    public List<Video> getVideos() throws JSONException, ExecutionException, InterruptedException {
        if (medias != null) return medias;
        medias = new ArrayList<>();
        Uri uri = Uri.parse(link);
        String segment = uri.getPathSegments().get(1);
        String filmId = segment.substring(segment.lastIndexOf('.')+1);
        String episodeId = uri.getPathSegments().get(2);
        JSONObject o = new JSONObject(new DownloadTask().execute(String.format(GRABBER_API, filmId, episodeId)).get());
        Uri.Builder url = Uri.parse(o.getString("grabber"))
                .buildUpon();
        JSONObject jsonparms = o.getJSONObject("params");
        for (int i = 0; i < jsonparms.length(); ++i) {
            String key = jsonparms.names().getString(i);
            url.appendQueryParameter(key, jsonparms.getString(key));
        }
        String mediaurls = url.build().toString();
        if (!mediaurls.startsWith("https://")) {
            if (!mediaurls.startsWith("//"))
                mediaurls = "https://" + mediaurls;
            else
                mediaurls = "https:" + mediaurls;
        }
        JSONArray jsonmedia = new JSONObject(new DownloadTask().execute(mediaurls).get()).getJSONArray("data");

        for (int i = 0; i < jsonmedia.length(); ++i) {
            medias.add(new Video(jsonmedia.getJSONObject(i).getString("label"), jsonmedia.getJSONObject(i).getString("file"), jsonmedia.getJSONObject(i).getString("type"), episodeId));
        }

        return medias;
    }

    protected Episode(Parcel in) {
        animeId = in.readString();
        episode = in.readString();
        link = in.readString();
        medias = in.createTypedArrayList(Video.CREATOR);
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(animeId);
        dest.writeString(episode);
        dest.writeString(link);
        dest.writeTypedList(medias);
    }
}
