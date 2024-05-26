package com.example.horizonapp.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class TopPlaceDomain implements Parcelable {
    private String title;
    private String picUrl;
    private String location;
    private boolean isFavorite;

    public TopPlaceDomain(String title, String picUrl, String location) {
        this.title = title;
        this.picUrl = picUrl;
        this.location = location;
    }

    protected TopPlaceDomain(Parcel in) {
        title = in.readString();
        picUrl = in.readString();
        location = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<TopPlaceDomain> CREATOR = new Creator<TopPlaceDomain>() {
        @Override
        public TopPlaceDomain createFromParcel(Parcel in) {
            return new TopPlaceDomain(in);
        }

        @Override
        public TopPlaceDomain[] newArray(int size) {
            return new TopPlaceDomain[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(picUrl);
        dest.writeString(location);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    public String getTitle() {
        return title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getLocation() {
        return location;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
