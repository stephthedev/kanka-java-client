package com.stephthedev.kankaclient.api.entities;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;

/**
 * Request an entity either by page number or link
 * Helps with pagination:
 * https://kanka.io/en-US/docs/1.0/pagination
 */
public class EntitiesRequest {
    
    private int page;
    private String link;
    private String lastSync;

    private EntitiesRequest(int page, String link, String lastSync) {
        this.page = page;
        this.link = link;
        this.lastSync = lastSync;
    }

    public int getPage() {
        return page;
    }
    public String getLink() {
        return link;
    }
    public String getLastSync() { return lastSync; }

    public static class Builder {
        public static final int UNSET = -1;

        public int page = UNSET;
        private String link;
        private String lastSync;

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public Builder withLink(String link) {
            this.link = link;
            return this;
        }

        /**
         * https://kanka.io/en-US/docs/1.0/last-sync
         * @param lastSync
         * @return
         */
        public Builder withLastSync(String lastSync) {
            this.lastSync = lastSync;
            return this;
        }

        public EntitiesRequest build() {
            boolean hasPage = (page > 0) ? true : false;
            boolean hasLink = (link != null && !link.trim().isEmpty()) ? true : false;
            boolean hasLastSync = (lastSync != null);

            if (lastSync != null) {
                //Throw the exception if it can't be parsed
                ZonedDateTime.parse(lastSync);
            }

            Preconditions.checkArgument(!(hasPage && hasLink),
                    "Cannot use both a page parameter and a link");

            return new EntitiesRequest(page, link, lastSync);
        }
    }
}
