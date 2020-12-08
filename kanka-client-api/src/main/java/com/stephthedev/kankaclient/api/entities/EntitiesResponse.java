package com.stephthedev.kankaclient.api.entities;

import com.stephthedev.kanka.generated.entities.KankaEntity;

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
    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    private Meta meta;
    private Links links;
    private List<T> data;
}
