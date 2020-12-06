package com.stephthedev.kankaclient.api;

/**
 * Request an entity either by page number or link
 * Helps with pagination:
 * https://kanka.io/en-US/docs/1.0/pagination
 */
public class EntityRequest {
    
    private int page;
    private String link;

    private EntityRequest(int page, String link) {
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

        public EntityRequest build() {
            boolean hasPage = (page > 0) ? true : false;
            boolean hasLink = (link != null && !link.trim().isEmpty()) ? true : false;

            if (hasPage) {
                //TODO: Check link is null
            } else if (hasLink) {
                //TODO: Check page is unset
            } else {
                //Do nothing
            }

            return new EntityRequest(page, link);
        }
    }
}
