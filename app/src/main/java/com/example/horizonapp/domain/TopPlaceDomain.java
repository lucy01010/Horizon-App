package com.example.horizonapp.domain;

import java.io.Serializable;

public class TopPlaceDomain implements Serializable {

    private final String title;
    private final String picUrl;
    private final String location;

    public TopPlaceDomain(String title, String picUrl, String location) {
        this.title = title;
        this.picUrl = picUrl;
        this.location = location;
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
}
