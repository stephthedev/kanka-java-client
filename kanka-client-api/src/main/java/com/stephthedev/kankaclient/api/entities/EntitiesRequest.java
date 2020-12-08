package com.stephthedev.kankaclient.api.entities;

/**
 * Request an entity either by page number or link
 * Helps with pagination:
 * https://kanka.io/en-US/docs/1.0/pagination
 */
public class EntitiesRequest {
    
    private int page;
    private String link;

    private EntitiesRequest(int page, String link) {
        this.page = page;
        this.link = link;
    }

    public int getPage() {
        return page;
    }
    public String getLink() {
        return link;
    }

    public static class Builder {
        public static final int UNSET = -1;

        public int page = UNSET;
        private String link;

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public Builder withLink(String link) {
            this.link = link;
            return this;
        }

        public EntitiesRequest build() {
            boolean hasPage = (page > 0) ? true : false;
            boolean hasLink = (link != null && !link.trim().isEmpty()) ? true : false;

            if (hasPage) {
                //TODO: Check link is null
            } else if (hasLink) {
                //TODO: Check page is unset
            } else {
                //Do nothing
            }

            return new EntitiesRequest(page, link);
        }
    }
}
