package com.example.horizonapp.domain;

import java.io.Serializable;

public class TopPlaceDomain implements Serializable {

    private final String title;
    private final String picUrl;
    private final String location;
    private boolean isFavorite;

    public TopPlaceDomain(String title, String picUrl, String location) {
        this.title = title;
        this.picUrl = picUrl;
        this.location = location;
        this.isFavorite = false;
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
