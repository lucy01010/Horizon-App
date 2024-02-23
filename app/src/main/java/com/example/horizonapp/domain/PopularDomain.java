package com.example.horizonapp.domain;

import java.io.Serializable;

public class PopularDomain {
    public static class Domain implements Serializable {
        private String domainTitle;
        private String domainPicUrl;
        private String domainLocation;

        public Domain(String title, String picUrl, String location) {
            this.domainTitle = title;
            this.domainPicUrl = picUrl;
            this.domainLocation = location;
        }

        public String getDomainTitle() {
            return domainTitle;
        }

        public void setDomainTitle(String title) {
            this.domainTitle = title;
        }

        public String getDomainPicUrl() {
            return domainPicUrl;
        }

        public void setDomainPicUrl(String picUrl) {
            this.domainPicUrl = picUrl;
        }

        public String getDomainLocation() {
            return domainLocation;
        }

        public void setDomainLocation(String location) {
            this.domainLocation = location;
        }
    }
}
