package com.ruto.nineanimeandroid.api.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ruto on 2/23/2017.
 */

public class Video implements Parcelable {
    private String tag;
    private String resolution;
    private String link;
    private String type;

    public Video(String resolution, String link, String type, String tag) {
        this.resolution = resolution;
        this.link = link;
        this.type = type;
        this.tag = tag;
    }

    public String getResolution() {
        return resolution;
    }

    public String getLink() {
        return link;
    }

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    protected Video(Parcel in) {
        resolution = in.readString();
        link = in.readString();
        type = in.readString();
        tag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resolution);
        dest.writeString(link);
        dest.writeString(type);
        dest.writeString(tag);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
