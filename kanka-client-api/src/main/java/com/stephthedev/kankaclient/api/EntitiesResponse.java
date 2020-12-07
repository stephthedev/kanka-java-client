package com.stephthedev.kankaclient.api;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.hateoas.KankaLinks;
import com.stephthedev.kanka.generated.hateoas.KankaMeta;

import java.util.List;

/**
 * A representation of the kanka response that looks like:
 * <pre>
 * {
 *     "links" : { ... },
 *     "meta" : { ... },
 *     "data" : { ... }
 * }
 * </pre>
 * @param <T>
 */
public class EntitiesResponse<T extends KankaEntity> {
    public KankaMeta getMeta() {
        return meta;
    }

    public void setMeta(KankaMeta meta) {
        this.meta = meta;
    }

    public KankaLinks getLinks() {
        return links;
    }

    public void setLinks(KankaLinks links) {
        this.links = links;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    private KankaMeta meta;
    private KankaLinks links;
    private List<T> data;
}
