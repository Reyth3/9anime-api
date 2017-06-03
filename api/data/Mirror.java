package com.ruto.nineanimeandroid.api.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruto on 2/23/2017.
 */

public class Mirror implements Parcelable {
    private String name;
    private List<Episode> episodes;

    public Mirror(String name, List<Episode> episodes) {
        this.name = name;
        this.episodes = episodes;
    }

    public String getName() {
        return name;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    protected Mirror(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            episodes = new ArrayList<Episode>();
            in.readList(episodes, Episode.class.getClassLoader());
        } else {
            episodes = null;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (episodes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(episodes);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Mirror> CREATOR = new Parcelable.Creator<Mirror>() {
        @Override
        public Mirror createFromParcel(Parcel in) {
            return new Mirror(in);
        }

        @Override
        public Mirror[] newArray(int size) {
            return new Mirror[size];
        }
    };
}
