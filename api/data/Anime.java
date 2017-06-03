package com.ruto.nineanimeandroid.api.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.ruto.nineanimeandroid.Session;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruto on 2/22/2017.
 */

public class Anime implements Parcelable {
    private String id;
    private String link;
    private String quality = "N/A";
    private String title = "N/A";
    private String imagelink;
    private List<Mirror> mirrors = new ArrayList<>();
    private double score = Double.MIN_VALUE;
    private String status = "N/A";
    private String date = "N/A";
    private List<String> genres = new ArrayList<>();
    private List<String> otherNames = new ArrayList<>();
    private String description = "N/A";
    private String descriptorLink;
    private boolean descriptorLoaded = false;
    private boolean favorited = false;

    public Anime(String title, String link, String imagelink) {
        this.id = link.substring(link.lastIndexOf('.') + 1);
        this.title = title;
        this.link = link;
        this.imagelink = imagelink;
    }

    public Anime(String title, String link, String imagelink, String descriptorLink) {
        this.id = link.substring(link.lastIndexOf('.') + 1);
        this.title = title;
        this.link = link;
        this.imagelink = imagelink;
        this.descriptorLink = descriptorLink;
    }

    protected Anime(Parcel in) {
        id = in.readString();
        link = in.readString();
        quality = in.readString();
        title = in.readString();
        imagelink = in.readString();
        mirrors = in.createTypedArrayList(Mirror.CREATOR);
        score = in.readDouble();
        status = in.readString();
        date = in.readString();
        genres = in.createStringArrayList();
        otherNames = in.createStringArrayList();
        description = in.readString();
        descriptorLink = in.readString();
        descriptorLoaded = in.readByte() != 0;
        favorited = in.readByte() != 0;
    }

    public static final Creator<Anime> CREATOR = new Creator<Anime>() {
        @Override
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public List<Mirror> getMirrors() {
        return mirrors;
    }

    public String getLink() {
        return link;
    }

    public String getImagelink() {
        return imagelink;
    }

    public double getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getOtherNames() {
        return otherNames;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getGenres() {
        return genres;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void favorite() {
        favorited = true;
        Session.getInstance().getFavorites().add(this);
    }

    public void unfavorite() {
        favorited = false;
        Session.getInstance().getFavorites().remove(this);
    }

    public String toJson() {
        return Session.getInstance().getGson().toJson(this);
    }

    public static Anime fromJson(String json) {
        return Session.getInstance().getGson().fromJson(json, Anime.class);
    }

    public void loadDescription() throws IOException {
        if (descriptorLoaded) return;
        descriptorLoaded = true;
        Document doc = Jsoup.connect(link).get();
        Element info = doc.select("div[id='info']").first();

        List<String> genres = new ArrayList<>();
        List<String> syns = new ArrayList<>();
        Elements cols = info.select("dl[class='meta col-sm-12']");
        String score = cols.get(1).select("dd").first().text().trim();
        score = score.substring(0, score.indexOf(' '));

        Elements elgenres = cols.first().select("dd").get(2).select("a");
        for (Element el : elgenres) {
            genres.add(el.text().trim());
        }
        Element elnames = cols.first().select("dd").first();
        for (String name : elnames.text().trim().split(";")) {
            syns.add(name.trim());
        }
        int statusIndex = cols.first().select("dt").get(3).text().trim().toLowerCase().startsWith("studio") ? 5 : 4;
        this.quality = info.select("span.quality").first().text();
        this.score = Double.parseDouble(score);
        this.status = cols.first().select("dd").get(statusIndex).text().trim();
        this.date = cols.first().select("dd").get(statusIndex-1).text().trim();
        this.genres = genres;
        this.otherNames = syns;
        this.description = info.select("div.desc").first().text().trim();
    }

    public void loadMirrors() throws IOException {
        if (mirrors.size() > 0) return;
        Document page = Jsoup.connect(link).get();
        Elements mirrors = page.select("div.server.row[data-type='direct']");
        for (Element mirror : mirrors) {
            List<Episode> episodes = new ArrayList<>();
            for (Element ep : mirror.select("li")) {
                episodes.add(new Episode(getId(), ep.select("a").first().text(), "https://9anime.to" + ep.select("a").first().attr("href")));
            }
            this.mirrors.add(new Mirror(mirror.select("label.name").text().trim(), episodes));
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Anime && id.equals(((Anime)o).id);
    }

    @Override
    public int hashCode() {
        int code = 1337;
        for (char c : id.toCharArray()) {
            code += c;
        }
        return code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(link);
        dest.writeString(quality);
        dest.writeString(title);
        dest.writeString(imagelink);
        dest.writeTypedList(mirrors);
        dest.writeDouble(score);
        dest.writeString(status);
        dest.writeString(date);
        dest.writeStringList(genres);
        dest.writeStringList(otherNames);
        dest.writeString(description);
        dest.writeString(descriptorLink);
        dest.writeByte((byte) (descriptorLoaded ? 1 : 0));
        dest.writeByte((byte) (favorited ? 1 : 0));
    }
}
